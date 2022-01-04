package com.example.mywebquizengine.service;

import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.projection.UserCommonView;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {
    public static UserCommonView getUserForMeeting(String usernameForOutput) {
        User user = MywebquizengineApplication.ctx.getBean(UserService.class).loadUserByUsername(usernameForOutput);

        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();

        return pf.createProjection(UserCommonView.class, user);
    }
}
