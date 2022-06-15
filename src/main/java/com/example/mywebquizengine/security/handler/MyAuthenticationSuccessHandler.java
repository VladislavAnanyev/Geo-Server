package com.example.mywebquizengine.security.handler;

import com.example.mywebquizengine.model.userinfo.RegistrationType;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.security.ActiveUserStore;
import com.example.mywebquizengine.security.LoggedUser;
import com.example.mywebquizengine.service.AuthService;
import com.example.mywebquizengine.service.utils.OauthUserMapper;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;


@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends
        AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private AuthService authService;

    public MyAuthenticationSuccessHandler() {
        super();
    }

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (authentication instanceof OAuth2AuthenticationToken) {

            // save if not exist (registration)
            User user = authService.processCheckIn(
                    OauthUserMapper.castToUserFromOauth(authentication),
                    RegistrationType.OAUTH2
            );

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            user, "Bearer", Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
            } else {
                targetUrl = "/profile";
            }
        } else if (authentication instanceof UsernamePasswordAuthenticationToken) {

            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
            } else {
                targetUrl = "/profile";
            }
        }

        LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);

        HttpSession session = request.getSession(false);

        if (session != null) {

            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("exchange", RabbitUtil.getExchangeName(authentication.getName()));
            session.setMaxInactiveInterval(60);
        } else {
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("exchange", RabbitUtil.getExchangeName(authentication.getName()));
            session.setMaxInactiveInterval(60);
        }

        //clearAuthenticationAttributes(request);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return;
        }

        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}

