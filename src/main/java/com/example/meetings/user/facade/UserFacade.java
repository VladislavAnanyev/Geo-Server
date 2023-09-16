package com.example.meetings.user.facade;

import com.example.meetings.user.model.dto.AuthUserView;
import com.example.meetings.user.model.dto.ProfileView;
import org.springframework.stereotype.Service;

@Service
public interface UserFacade {
    AuthUserView getAuthUser(Long userId);
    ProfileView getUserProfileById(Long userId);
}
