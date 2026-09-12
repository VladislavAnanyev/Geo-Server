package com.example.meetings.user.facade;

import com.example.meetings.user.favoriteplace.model.AddFavoritePlaceRequest;
import com.example.meetings.user.favoriteplace.model.dto.FavoritePlaceDto;
import com.example.meetings.user.model.ChangeUserRequest;
import com.example.meetings.user.model.dto.AuthUserView;
import com.example.meetings.user.model.dto.UserProfileDto;

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
    UserProfileDto getUserProfileById(Long userId);

    /**
     * Обновляет поля профиля пользователя с проверкой текста на модерацию.
     *
     * @param userId  идентификатор пользователя
     * @param request новые значения полей профиля
     */
    void changeUser(Long userId, ChangeUserRequest request);

    /**
     * Добавляет любимое место в профиль пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param request название и координаты места
     * @return сохранённое место
     */
    FavoritePlaceDto addFavoritePlace(Long userId, AddFavoritePlaceRequest request);

    /**
     * Удаляет любимое место из профиля пользователя.
     *
     * @param userId          идентификатор пользователя
     * @param favoritePlaceId идентификатор записи
     */
    void removeFavoritePlace(Long userId, Long favoritePlaceId);
}
