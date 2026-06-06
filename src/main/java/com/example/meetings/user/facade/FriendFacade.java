package com.example.meetings.user.facade;

import com.example.meetings.user.model.dto.UserCommonView;

import java.util.List;

/**
 * Фасад сценариев API для работы со списком друзей.
 */
public interface FriendFacade {

    /**
     * Возвращает список друзей текущего пользователя.
     */
    List<UserCommonView> getFriends(Long authUserId);

    /**
     * Удаляет друга и отзывает связанные разрешения на обмен геолокацией.
     */
    void deleteFriend(Long friendUserId, Long authUserId);
}
