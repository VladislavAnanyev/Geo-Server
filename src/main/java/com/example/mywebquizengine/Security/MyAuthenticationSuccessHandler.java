package com.example.mywebquizengine.Security;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;


@Component("myAuthenticationSuccessHandler")
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

/*    @Autowired
    LoggedUser loggedUser;*/

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {


        System.out.println("Саксес хендлер");
        HttpSession session = request.getSession(false);
        if (session != null) {

            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            session.invalidate();
            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(5);
        } else {
            LoggedUser user = new LoggedUser(authentication.getName(), activeUserStore);
            //session = request.getSession(true);

            session = request.getSession(true);
            session.setAttribute("user", user);
            session.setMaxInactiveInterval(60);
        }
        //session.setMaxInactiveInterval(10);

        /*if (session != null) {
            LoggedUser user = new LoggedUser(authentication.getName(), loggedUser.getUsers());
            session.setAttribute("user", user);
        }
*/


       /* User user = userRepository.findById(authentication.getName()).get();
        user.setOnline("true");
        userRepository.save(user);

        HttpSession session = request.getSession(true);
        session.setMaxInactiveInterval(10);
        session.setAttribute("user", user.getUsername());
*/
        redirectStrategy.sendRedirect(request, response, "/profile");
        }
}

