package com.example.meetings.location.facade;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.chat.model.CreateGroupModel;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.SubscriptionRequiredException;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.location.model.Location;
import com.example.meetings.location.model.dto.LocationDTO;
import com.example.meetings.location.model.dto.LocationRequest;
import com.example.meetings.location.service.LocationService;
import com.example.meetings.subscription.service.SubscriptionService;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.UserCommonView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.example.meetings.util.Constants.ADMIN_ID;

/**
 * Фасад сценариев API локаций: создание районных чатов и управление участниками
 * по геолокации.
 */
@Service
@Slf4j
public class LocationFacade {

    private final LocationService locationService;
    private final GeolocationService geolocationService;
    private final MessageService messageService;
    private final SubscriptionService subscriptionService;
    private final ProjectionUtil projectionUtil;

    public LocationFacade(LocationService locationService,
            GeolocationService geolocationService,
            MessageService messageService,
            SubscriptionService subscriptionService,
            ProjectionUtil projectionUtil) {
        this.locationService = locationService;
        this.geolocationService = geolocationService;
        this.messageService = messageService;
        this.subscriptionService = subscriptionService;
        this.projectionUtil = projectionUtil;
    }

    /**
     * Создаёт локацию с групповым чатом; доступно только администратору.
     *
     * @param request   параметры новой локации
     * @param principal данные текущего пользователя
     * @return созданная локация
     */
    @Transactional
    public LocationDTO createLocation(LocationRequest request, AuthUserDetails principal) {
        if (principal.getUserId() != ADMIN_ID) {
            throw new UnsupportedOperationException("You are not allowed to create location");
        }

        Dialog dialog = messageService.createGroup(
                new CreateGroupModel()
                        .setName(request.getName())
                        .setImage(request.getImage())
                        .setAuthUserId(principal.getUserId())
                        .setUsers(List.of(principal.getUserId())));
        Location location = locationService.save(request, dialog);
        messageService.saveMessage(
                new SendMessageModel()
                        .setSenderId(principal.getUserId())
                        .setContent("Это чат локации " + location.getName()
                                + ". Здесь могут писать все кто в этом районе прямо сейчас")
                        .setDialogId(dialog.getDialogId()));

        return new LocationDTO(location);
    }

    /**
     * Возвращает все локации с количеством участников в групповом чате.
     *
     * @return список локаций
     */
    public List<LocationDTO> getLocations() {
        return locationService.findAll().stream()
                .map(location -> {
                    location.setPeopleCount(
                            (int) location.getDialog().getUsers().stream()
                                    .filter(user -> !user.getUserId().equals(ADMIN_ID))
                                    .count());
                    return new LocationDTO(location);
                })
                .toList();
    }

    /**
     * Возвращает участников чата локации; требуется премиум-подписка.
     *
     * @param authUserId идентификатор текущего пользователя
     * @param locationId идентификатор локации
     * @return список пользователей в локации без служебного аккаунта администратора
     */
    @Transactional(readOnly = true)
    public List<UserCommonView> getLocationPeople(Long authUserId, Long locationId) {
        if (!subscriptionService.isPremium(authUserId)) {
            throw new SubscriptionRequiredException(
                    "exception.subscription.location.people.premium.required",
                    GlobalErrorCode.ERROR_SUBSCRIPTION_LOCATION_PEOPLE);
        }

        Location location = locationService.findById(locationId);
        return location.getDialog().getUsers().stream()
                .filter(user -> !user.getUserId().equals(ADMIN_ID))
                .map(user -> projectionUtil.parse(user, UserCommonView.class))
                .toList();
    }

    /**
     * По расписанию добавляет в чат локации пользователей, находящихся в её
     * радиусе.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    @Async
    public void findUsersInLocationAndAddDialogUsers() {
        List<Location> locations = locationService.findAll();
        for (Location location : locations) {
            List<Geolocation> geolocations = geolocationService.findInSquare(
                    new Geolocation().setLng(location.getLng()).setLat(location.getLat()),
                    location.getRadius(), LocalDateTime.now());
            geolocations.stream().map(geolocation -> geolocation.getUser().getUserId()).forEach(userId -> {
                if (!location.getDialog().getUsers().stream().map(User::getUserId).toList().contains(userId)) {
                    log.info("Добавляю пользователя {} в диалог для локации {}", userId, location.getName());
                    messageService.addUserToGroupDialog(userId, location.getDialog().getDialogId());
                }
            });
        }
    }

    /**
     * По расписанию удаляет из чата локации пользователей, покинувших её радиус.
     */
    @Scheduled(cron = "30 */5 * * * *")
    @Transactional
    @Async
    public void findUsersInLocationAndDeleteDialogUsers() {
        List<Location> locations = locationService.findAll();
        for (Location location : locations) {
            List<Geolocation> geolocations = geolocationService.findInSquare(
                    new Geolocation().setLng(location.getLng()).setLat(location.getLat()),
                    location.getRadius(), LocalDateTime.now(), 5L);

            List<Long> usersInArea = geolocations.stream()
                    .map(Geolocation::getUser)
                    .map(User::getUserId)
                    .toList();
            List<Long> dialogUserIds = new ArrayList<>(
                    location.getDialog().getUsers().stream().map(User::getUserId).toList());
            for (Long userId : dialogUserIds) {
                if (!usersInArea.contains(userId) && !userId.equals(ADMIN_ID)) {
                    log.info("Удаляю пользователя {} из диалога локации {}", userId, location.getName());
                    messageService.deleteUserFromGroupDialog(userId, location.getDialog().getDialogId());
                }
            }
        }
    }
}
