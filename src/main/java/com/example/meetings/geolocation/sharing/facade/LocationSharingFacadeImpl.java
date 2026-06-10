package com.example.meetings.geolocation.sharing.facade;

import com.example.meetings.common.rabbit.eventtype.LocationSharingType;
import com.example.meetings.common.service.EventService;
import com.example.meetings.common.service.NotificationService;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharing;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus;
import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import com.example.meetings.geolocation.sharing.model.dto.output.GetLocationSharingListResponse;
import com.example.meetings.geolocation.sharing.model.dto.output.LocationSharingView;
import com.example.meetings.geolocation.sharing.service.LocationSharingService;
import com.example.meetings.user.model.dto.UserCommonView;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.example.meetings.util.Constants.NOTIFICATION_LOCATION_SHARING_DESCRIPTION;
import static com.example.meetings.util.Constants.NOTIFICATION_LOCATION_SHARING_TITLE;
import static com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.ACTIVE;
import static com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.PENDING;
import static java.util.stream.Collectors.toList;

/**
 * Оркестрация обмена геолокацией: сервис, события Rabbit и push-уведомления.
 */
@Service
@RequiredArgsConstructor
public class LocationSharingFacadeImpl implements LocationSharingFacade {

    private final LocationSharingService locationSharingService;
    private final ProjectionUtil projectionUtil;
    private final EventService eventService;
    private final NotificationService notificationService;

    /**
     * Отправляет приглашение на обмен геолокацией другу.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    идентификатор друга для приглашения
     */
    @Override
    @Transactional
    public void sendInvite(Long authUserId, LocationSharingFriendRequest request) {
        LocationSharing sharing = locationSharingService.sendInvite(authUserId, request.getFriendUserId());
        if (PENDING.equals(sharing.getStatus())) {
            LocationSharingView view = projectionUtil.parse(sharing, LocationSharingView.class);
            eventService.send(view, sharing.getUsers(), LocationSharingType.LOCATION_SHARING_REQUEST);
            notificationService.send(NOTIFICATION_LOCATION_SHARING_TITLE, NOTIFICATION_LOCATION_SHARING_DESCRIPTION, sharing);
        } else if (ACTIVE.equals(sharing.getStatus())) {
            notifyAccepted(sharing);
        }
    }

    /**
     * Возвращает входящие приглашения в статусе PENDING.
     *
     * @param authUserId   идентификатор текущего пользователя
     * @param userIdFilter опциональный идентификатор отправителя для фильтрации
     * @return список входящих приглашений
     */
    @Override
    @Transactional(readOnly = true)
    public GetLocationSharingListResponse getIncomingPending(Long authUserId, Long userIdFilter) {
        return new GetLocationSharingListResponse()
                .setItems(locationSharingService.getIncomingPending(authUserId, userIdFilter));
    }

    /**
     * Возвращает исходящие приглашения в статусе PENDING.
     *
     * @param authUserId   идентификатор текущего пользователя
     * @param userIdFilter опциональный идентификатор получателя для фильтрации
     * @return список исходящих приглашений
     */
    @Override
    @Transactional(readOnly = true)
    public GetLocationSharingListResponse getOutgoingPending(Long authUserId, Long userIdFilter) {
        return new GetLocationSharingListResponse()
                .setItems(locationSharingService.getOutgoingPending(authUserId, userIdFilter));
    }

    /**
     * Возвращает активные записи обмена для текущего пользователя.
     *
     * @param authUserId идентификатор текущего пользователя
     * @return список активных обменов геолокацией
     */
    @Override
    @Transactional(readOnly = true)
    public GetLocationSharingListResponse getActive(Long authUserId) {
        List<LocationSharingView> items = locationSharingService.findAllActiveForUser(authUserId).stream()
                .map(sharing -> projectionUtil.parse(sharing, LocationSharingView.class))
                .collect(toList());
        return new GetLocationSharingListResponse().setItems(items);
    }

    /**
     * Принимает приглашение по идентификатору записи.
     *
     * @param authUserId идентификатор получателя приглашения
     * @param sharingId  идентификатор записи обмена
     */
    @Override
    @Transactional
    public void accept(Long authUserId, Long sharingId) {
        LocationSharing sharing = locationSharingService.accept(sharingId, authUserId);
        notifyAccepted(sharing);
    }

    /**
     * Отклоняет приглашение по идентификатору записи.
     *
     * @param authUserId идентификатор получателя приглашения
     * @param sharingId  идентификатор записи обмена
     */
    @Override
    @Transactional
    public void reject(Long authUserId, Long sharingId) {
        locationSharingService.reject(sharingId, authUserId);
    }

    /**
     * Отзывает активный обмен с выбранным другом.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param request    идентификатор друга, с которым прекращается обмен
     */
    @Override
    @Transactional
    public void revoke(Long authUserId, LocationSharingFriendRequest request) {
        locationSharingService.revoke(authUserId, request.getFriendUserId());
    }

    /**
     * Отправляет событие о принятии обоим участникам и push-уведомление.
     */
    private void notifyAccepted(LocationSharing sharing) {
        LocationSharingView view = projectionUtil.parse(sharing, LocationSharingView.class);
        UserCommonView initiatorView = projectionUtil.parse(sharing.getInitiator(), UserCommonView.class);
        UserCommonView targetView = projectionUtil.parse(sharing.getTarget(), UserCommonView.class);
        eventService.send(initiatorView, Set.of(sharing.getTarget()), LocationSharingType.LOCATION_SHARING_ACCEPTED);
        eventService.send(targetView, Set.of(sharing.getInitiator()), LocationSharingType.LOCATION_SHARING_ACCEPTED);
        notificationService.send(NOTIFICATION_LOCATION_SHARING_TITLE, NOTIFICATION_LOCATION_SHARING_DESCRIPTION, sharing);
    }
}
