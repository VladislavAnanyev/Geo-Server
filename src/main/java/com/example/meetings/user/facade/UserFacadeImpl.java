package com.example.meetings.user.facade;

import com.example.meetings.user.model.dto.AuthUserView;
import com.example.meetings.user.model.dto.ProfileView;
import com.example.meetings.user.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;

    public UserFacadeImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public AuthUserView getAuthUser(Long userId) {
        return userService.getAuthUserInfo(userId);
    }

    @Override
    public ProfileView getUserProfileById(Long userId) {
        return userService.getUserProfileById(userId);
    }
}
