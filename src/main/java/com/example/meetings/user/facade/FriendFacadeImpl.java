package com.example.meetings.user.facade;

import com.example.meetings.geolocation.sharing.service.LocationSharingService;
import com.example.meetings.user.model.dto.UserCommonView;
import com.example.meetings.user.service.FriendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.meetings.util.Constants.ADMIN_ID;

/**
 * Реализация фасада друзей: список друзей и удаление с отзывом геолокации.
 */
@Service
@RequiredArgsConstructor
public class FriendFacadeImpl implements FriendFacade {

    private final FriendService friendService;
    private final LocationSharingService locationSharingService;

    /**
     * Возвращает друзей пользователя без служебного аккаунта администратора.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return список друзей без администратора
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserCommonView> getFriends(Long authUserId) {
        List<UserCommonView> friends = friendService.findMyFriends(authUserId);
        friends.removeIf(user -> user.getUserId().equals(ADMIN_ID));
        return friends;
    }

    /**
     * Отзывает обмен геолокацией и удаляет пользователя из друзей.
     *
     * @param friendUserId идентификатор удаляемого друга
     * @param authUserId   идентификатор текущего пользователя
     */
    @Override
    @Transactional
    public void deleteFriend(Long friendUserId, Long authUserId) {
        locationSharingService.revokeAllBetween(authUserId, friendUserId);
        friendService.deleteFriend(friendUserId, authUserId);
    }
}
