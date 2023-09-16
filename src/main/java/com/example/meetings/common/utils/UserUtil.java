package com.example.meetings.common.utils;

import com.example.meetings.MeetingsApplication;
import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.UserCommonView;
import com.example.meetings.user.service.UserService;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

public class UserUtil {
    public static UserCommonView getUserForMeeting(Long firstUserId, Long secondUserId) {

        Long authName = ((AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId();
        User user;
        if (authName.equals(firstUserId)) {
            user = MeetingsApplication.ctx.getBean(UserService.class).loadUserByUserId(secondUserId);
        } else if (authName.equals(secondUserId)) {
            user = MeetingsApplication.ctx.getBean(UserService.class).loadUserByUserId(firstUserId);
        } else  {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        ProjectionFactory pf = new SpelAwareProxyProjectionFactory();
        return pf.createProjection(UserCommonView.class, user);
    }
}
