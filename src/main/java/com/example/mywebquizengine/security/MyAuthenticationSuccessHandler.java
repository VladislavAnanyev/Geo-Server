package com.example.mywebquizengine.security;

import com.example.mywebquizengine.model.RegistrationType;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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


@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler extends
        AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private UserService userService;

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
            User user = userService.castToUserFromOauth((OAuth2AuthenticationToken) authentication);
            userService.processCheckIn(user, RegistrationType.OAUTH2); // save if not exist (registration)
            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
            } else {
                targetUrl = "/profile";
            }
        }

        if (authentication instanceof UsernamePasswordAuthenticationToken) {

            if (savedRequest != null) {
                targetUrl = savedRequest.getRedirectUrl();
            } else {
                targetUrl = "/profile";
            }
        }



        HttpSession session = request.getSession(false);
        if (session != null) {

            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60);
            session.setAttribute("abc", "AAA");
        } else {
            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);

            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60);
            session.setAttribute("abc", "AAA");
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

