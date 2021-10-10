/*
package com.example.mywebquizengine.Security;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.Repos.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;




@Configuration
public class HttpSessionConfig {

    @Bean                           // bean for http session listener
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {               // This method will be called when session created
                System.out.println("Session Created with session id+" + se.getSession().getId());
                User authUser = MywebquizengineApplication.ctx.getBean(UserRepository.class).findById((String) se.getSession().getAttribute("user")).get();
                authUser.setOnline("true");
                MywebquizengineApplication.ctx.getBean(UserRepository.class).save(authUser);
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {         // This method will be automatically called when session destroyed
                System.out.println("Session Destroyed, Session id:" + se.getSession().getId());
                User authUser = MywebquizengineApplication.ctx.getBean(UserRepository.class).findById((String) se.getSession().getAttribute("user")).get();
                authUser.setOnline("false");
                MywebquizengineApplication.ctx.getBean(UserRepository.class).save(authUser);
            }
        };
    }

    @Bean                   // bean for http session attribute listener
    public HttpSessionAttributeListener httpSessionAttributeListener() {
        return new HttpSessionAttributeListener() {
            @Override
            public void attributeAdded(HttpSessionBindingEvent se) {            // This method will be automatically called when session attribute added
                System.out.println("Attribute Added following information");
                System.out.println("Attribute Name:" + se.getName());
                System.out.println("Attribute Value:" + se.getName());
            }

            @Override
            public void attributeRemoved(HttpSessionBindingEvent se) {      // This method will be automatically called when session attribute removed
                System.out.println("attributeRemoved");
            }

            @Override
            public void attributeReplaced(HttpSessionBindingEvent se) {     // This method will be automatically called when session attribute replace
                System.out.println("Attribute Replaced following information");
                System.out.println("Attribute Name:" + se.getName());
                System.out.println("Attribute Old Value:" + se.getValue());
            }
        };
    }
}
*/
