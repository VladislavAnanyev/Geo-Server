package com.example.meetings.user.facade;

import com.example.meetings.moderation.TextModerationService;
import com.example.meetings.user.model.ChangeUserRequest;
import com.example.meetings.user.model.dto.AuthUserView;
import com.example.meetings.user.model.dto.ProfileView;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import static com.example.meetings.util.Constants.MODERATION_FIELD_DESCRIPTION;
import static com.example.meetings.util.Constants.MODERATION_FIELD_FIRST_NAME;

@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final TextModerationService textModerationService;

    @Override
    public AuthUserView getAuthUser(Long userId) {
        return userService.getAuthUserInfo(userId);
    }

    @Override
    public ProfileView getUserProfileById(Long userId) {
        return userService.getUserProfileById(userId);
    }

    @Override
    public void changeUser(Long userId, ChangeUserRequest request) {
        if (StringUtils.isNotBlank(request.getFirstName())) {
            textModerationService.validateUserProfileText(MODERATION_FIELD_FIRST_NAME, request.getFirstName());
        }
        if (StringUtils.isNotBlank(request.getDescription())) {
            textModerationService.validateUserProfileText(MODERATION_FIELD_DESCRIPTION, request.getDescription());
        }
        userService.changeUser(userId, request);
    }
}
