package com.example.mywebquizengine.security.handler;

import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.security.ActiveUserStore;
import com.example.mywebquizengine.security.LoggedUser;
import com.example.mywebquizengine.service.AuthService;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
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

            /*
              Автоматическая регистрация при входе через oauth2 если пользователь еще не зарегистирован
             */
            authService.signInViaExternalServiceToken(authentication);

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

        if (targetUrl.contains("test") || targetUrl.contains("jwt") || targetUrl.contains("Geolocation")) {
            targetUrl = "/profile";
        }

        User principal = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        LoggedUser user = new LoggedUser(principal.getUserId(), activeUserStore);
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("exchange", RabbitUtil.getExchangeName(principal.getUserId()));
            session.setMaxInactiveInterval(60);
        } else {
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setAttribute("exchange", RabbitUtil.getExchangeName(principal.getUserId()));
            session.setMaxInactiveInterval(60);
        }
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }
}

