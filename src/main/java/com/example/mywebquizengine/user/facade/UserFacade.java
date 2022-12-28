package com.example.mywebquizengine.user.facade;

import com.example.mywebquizengine.user.model.dto.AuthUserView;
import com.example.mywebquizengine.user.model.dto.ProfileView;
import org.springframework.stereotype.Service;

@Service
public interface UserFacade {
    AuthUserView getAuthUser(Long userId);
    ProfileView getUserProfileById(Long userId);
}
