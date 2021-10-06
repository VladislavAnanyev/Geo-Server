/*
package com.example.mywebquizengine.Security;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.Repos.UserRepository;
import org.apache.catalina.session.StandardSession;
import org.apache.catalina.session.StandardSessionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.*;
import java.security.Principal;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

@WebListener
public class SessionEventListener implements HttpSessionListener {

    private static final Logger LOG= LoggerFactory.getLogger(SessionEventListener.class);

 */
/*private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void sessionCreated(HttpSessionEvent se) {

        LOG.info("New session is created. Adding Session to the counter.");
        counter.incrementAndGet();  //incrementing the counter
        updateSessionCounter(se);
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        LOG.info("Session destroyed. Removing the Session from the counter.");
        counter.decrementAndGet();  //decrementing counter
        updateSessionCounter(se);
    }

    private void updateSessionCounter(HttpSessionEvent httpSessionEvent){
        //Let's set in the context
        httpSessionEvent.getSession().getServletContext()
                .setAttribute("activeSession", counter.get());
        LOG.info("Total active session are {} ",counter.get());
    }*//*



    @Override
    public void sessionCreated(HttpSessionEvent event) {
        //super.sessionCreated(event);

        // ... Прочая логика
        //Установка таймаута сессии
//        SecurityContextHolder principal = MywebquizengineApplication.ctx.getBean(SecurityContextHolder.class);

        //HttpServletRequest httpRequest = MywebquizengineApplication.ctx.getBean(HttpServletRequest.class);

   */
/*     String name=null;



        HttpSession httpSession = event.getSession();

        System.out.println("User: " + httpSession.getAttribute("user"));
        //----Находим login пользователя с помощью SessionRegistry
        SessionRegistry sessionRegistry = MywebquizengineApplication.ctx.getBean(SessionRegistry.class);

        SessionInformation sessionInfo = sessionRegistry.getSessionInformation(event.getSession().getId());
        UserDetails ud = null;

        if (sessionInfo != null) ud = (UserDetails) sessionInfo.getPrincipal();
        if (ud != null) {
            name=ud.getUsername();
            //Удаляем запись об игроке и извещаем соперников, что мы ушли
            User user = MywebquizengineApplication.ctx.getBean(UserRepository.class).findById(name).get();
            user.setOnline("true");
            MywebquizengineApplication.ctx.getBean(UserRepository.class).save(user);
        }*//*


*/
/*

        StandardSessionFacade httpSession = (StandardSessionFacade) event.getSession();

        Enumeration<String> attributeNames = httpSession.getAttributeNames();*//*


        //if (standardSession.getManager().getContext().getDisplayName() != null) {
           // String name = (standardSession.getManager().getContext().getDisplayName());
           // User user = MywebquizengineApplication.ctx.getBean(UserRepository.class).findById(name).get();
           // user.setOnline("true");
           // MywebquizengineApplication.ctx.getBean(UserRepository.class).save(user);
        //}





*/
/*
        System.out.println(event.getSession().getMaxInactiveInterval());
        event.getSession().setMaxInactiveInterval(20);
        System.out.println(event.getSession().getMaxInactiveInterval());*//*



    }


    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        //System.out.println("ОЛОЛО2");
        System.out.println("ОЛОЛО2");
        String name=null;
        //----Находим login пользователя с помощью SessionRegistry
        SessionRegistry sessionRegistry = MywebquizengineApplication.ctx.getBean(SessionRegistry.class);
        SessionInformation sessionInfo = (sessionRegistry != null ? sessionRegistry
                .getSessionInformation(event.getSession().getId()) : null);
        UserDetails ud = null;
        if (sessionInfo != null) ud = (UserDetails) sessionInfo.getPrincipal();
        if (ud != null) {
            name=ud.getUsername();
            //Удаляем запись об игроке и извещаем соперников, что мы ушли
            User user = MywebquizengineApplication.ctx.getBean(UserRepository.class).findById(name).get();
            user.setOnline("false");
            MywebquizengineApplication.ctx.getBean(UserRepository.class).save(user);
        }
        //super.sessionDestroyed(event);
    }

    //По другому в слушатель сессии бины не заинжектишь
 */
/*public SessionRegistry getAnyBean(HttpSessionEvent event, String name){
        HttpSession session = event.getSession();
        ApplicationContext ctx =
                WebApplicationContextUtils.
                        getWebApplicationContext(session.getServletContext());
        return (SessionRegistry) ctx.getBean(name);
    }*//*



}
*/
