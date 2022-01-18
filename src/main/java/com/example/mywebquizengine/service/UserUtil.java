package com.example.mywebquizengine.service;

import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.model.projection.UserCommonView;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {
    public static UserCommonView getUserForMeeting(String firstUsername, String secondUsername) {

        String authName = SecurityContextHolder.getContext().getAuthentication().getName();
        User user;
        if (authName.equals(firstUsername)) {
            user = MywebquizengineApplication.ctx.getBean(UserService.class).loadUserByUsername(secondUsername);
        } else if (authName.equals(secondUsername)) {
            user = MywebquizengineApplication.ctx.getBean(UserService.class).loadUserByUsername(firstUsername);
        } else  {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();

        return pf.createProjection(UserCommonView.class, user);
    }
}
