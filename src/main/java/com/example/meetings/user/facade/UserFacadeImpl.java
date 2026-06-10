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

/**
 * Оркестрация профиля пользователя: чтение данных и обновление с модерацией текста.
 */
@Service
@RequiredArgsConstructor
public class UserFacadeImpl implements UserFacade {

    private final UserService userService;
    private final TextModerationService textModerationService;

    /**
     * Возвращает данные авторизованного пользователя для клиента.
     *
     * @param userId идентификатор пользователя
     * @return представление пользователя в контексте аутентификации
     */
    @Override
    public AuthUserView getAuthUser(Long userId) {
        return userService.getAuthUserInfo(userId);
    }

    /**
     * Возвращает публичный профиль пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return представление профиля пользователя
     */
    @Override
    public ProfileView getUserProfileById(Long userId) {
        return userService.getUserProfileById(userId);
    }

    /**
     * Обновляет поля профиля пользователя с проверкой текста на модерацию.
     *
     * @param userId  идентификатор пользователя
     * @param request новые значения полей профиля
     */
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
