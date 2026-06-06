package com.example.meetings.geolocation.sharing.facade;

import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import com.example.meetings.geolocation.sharing.model.dto.output.GetLocationSharingListResponse;

/**
 * Фасад сценариев API обмена геолокацией.
 */
public interface LocationSharingFacade {

    /** Отправляет приглашение на обмен геолокацией другу. */
    void sendInvite(Long authUserId, LocationSharingFriendRequest request);

    /**
     * Возвращает входящие приглашения в статусе PENDING.
     *
     * @param userIdFilter опциональный идентификатор отправителя для фильтрации
     */
    GetLocationSharingListResponse getIncomingPending(Long authUserId, Long userIdFilter);

    /**
     * Возвращает исходящие приглашения в статусе PENDING.
     *
     * @param userIdFilter опциональный идентификатор получателя для фильтрации
     */
    GetLocationSharingListResponse getOutgoingPending(Long authUserId, Long userIdFilter);

    /** Возвращает активные записи обмена для текущего пользователя. */
    GetLocationSharingListResponse getActive(Long authUserId);

    /** Принимает приглашение по идентификатору записи. */
    void accept(Long authUserId, Long sharingId);

    /** Отклоняет приглашение по идентификатору записи. */
    void reject(Long authUserId, Long sharingId);

    /** Отзывает активный обмен с выбранным другом. */
    void revoke(Long authUserId, LocationSharingFriendRequest request);
}
