package com.example.meetings.user.facade;

import com.example.meetings.user.model.dto.UserCommonView;

import java.util.List;

/**
 * Фасад сценариев API для работы со списком друзей.
 */
public interface FriendFacade {

    /**
     * Возвращает список друзей текущего пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return список друзей
     */
    List<UserCommonView> getFriends(Long authUserId);

    /**
     * Удаляет друга и отзывает связанные разрешения на обмен геолокацией.
     *
     * @param friendUserId идентификатор удаляемого друга
     * @param authUserId   идентификатор текущего пользователя
     */
    void deleteFriend(Long friendUserId, Long authUserId);
}
