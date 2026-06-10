package com.example.meetings.user.facade;

import com.example.meetings.user.model.ChangeUserRequest;
import com.example.meetings.user.model.dto.AuthUserView;
import com.example.meetings.user.model.dto.ProfileView;

/**
 * Фасад сценариев профиля и данных пользователя.
 */
public interface UserFacade {

    /**
     * Возвращает данные авторизованного пользователя для клиента.
     *
     * @param userId идентификатор пользователя
     * @return представление пользователя в контексте аутентификации
     */
    AuthUserView getAuthUser(Long userId);

    /**
     * Возвращает публичный профиль пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return представление профиля пользователя
     */
    ProfileView getUserProfileById(Long userId);

    /**
     * Обновляет поля профиля пользователя с проверкой текста на модерацию.
     *
     * @param userId  идентификатор пользователя
     * @param request новые значения полей профиля
     */
    void changeUser(Long userId, ChangeUserRequest request);
}
