package com.example.meetings.geolocation.sharing.facade;

import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import com.example.meetings.geolocation.sharing.model.dto.output.GetLocationSharingListResponse;

/**
 * Фасад сценариев API обмена геолокацией.
 */
public interface LocationSharingFacade {

    /**
     * Отправляет приглашение на обмен геолокацией другу.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    идентификатор друга для приглашения
     */
    void sendInvite(Long authUserId, LocationSharingFriendRequest request);

    /**
     * Возвращает входящие приглашения в статусе PENDING.
     *
     * @param authUserId   идентификатор текущего пользователя
     * @param userIdFilter опциональный идентификатор отправителя для фильтрации
     * @return список входящих приглашений
     */
    GetLocationSharingListResponse getIncomingPending(Long authUserId, Long userIdFilter);

    /**
     * Возвращает исходящие приглашения в статусе PENDING.
     *
     * @param authUserId   идентификатор текущего пользователя
     * @param userIdFilter опциональный идентификатор получателя для фильтрации
     * @return список исходящих приглашений
     */
    GetLocationSharingListResponse getOutgoingPending(Long authUserId, Long userIdFilter);

    /**
     * Возвращает активные записи обмена для текущего пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return список активных обменов геолокацией
     */
    GetLocationSharingListResponse getActive(Long authUserId);

    /**
     * Принимает приглашение по идентификатору записи.
     *
     * @param authUserId идентификатор получателя приглашения
     * @param sharingId  идентификатор записи обмена
     */
    void accept(Long authUserId, Long sharingId);

    /**
     * Отклоняет приглашение по идентификатору записи.
     *
     * @param authUserId идентификатор получателя приглашения
     * @param sharingId  идентификатор записи обмена
     */
    void reject(Long authUserId, Long sharingId);

    /**
     * Отзывает активный обмен с выбранным другом.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    идентификатор друга, с которым прекращается обмен
     */
    void revoke(Long authUserId, LocationSharingFriendRequest request);
}
