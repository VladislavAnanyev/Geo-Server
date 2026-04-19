package com.example.meetings.location.facade;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.chat.model.CreateGroupModel;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.service.MessageService;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.location.model.Location;
import com.example.meetings.location.model.dto.LocationDTO;
import com.example.meetings.location.model.dto.LocationRequest;
import com.example.meetings.location.service.LocationService;
import com.example.meetings.user.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.example.meetings.util.Constants.ADMIN_ID;

@Service
@Slf4j
public class LocationFacade {

    private final LocationService locationService;
    private final GeolocationService geolocationService;
    private final MessageService messageService;

    public LocationFacade(LocationService locationService, GeolocationService geolocationService, MessageService messageService) {
        this.locationService = locationService;
        this.geolocationService = geolocationService;
        this.messageService = messageService;
    }

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
                        .setUsers(List.of(principal.getUserId()))
        );
        Location location = locationService.save(request, dialog);
        messageService.saveMessage(
                new SendMessageModel()
                        .setSenderId(principal.getUserId())
                        .setContent("Это чат локации " + location.getName() + ". Здесь могут писать все кто в этом районе прямо сейчас")
                        .setDialogId(dialog.getDialogId())
        );

        return new LocationDTO(location);
    }

    public List<LocationDTO> getLocations() {
        return locationService.findAll().stream()
                .map(location -> {
                    location.setPeopleCount(location.getDialog().getUsers().size());
                    return new LocationDTO(location);
                })
                .toList();
    }

    @Scheduled(cron = "0 * * * * *")
    @Transactional
    @Async
    public void findUsersInLocationAndAddDialogUsers() {
        List<Location> locations = locationService.findAll();
        for (Location location : locations) {
            List<Geolocation> geolocations = geolocationService.findInSquare(
                    new Geolocation().setLng(location.getLng()).setLat(location.getLat()),
                    location.getRadius(), LocalDateTime.now()
            );
            geolocations.stream().map(geolocation -> geolocation.getUser().getUserId()).forEach(userId -> {
                if (!location.getDialog().getUsers().stream().map(User::getUserId).toList().contains(userId)) {
                    log.info("Добавляю пользователя {} в диалог для локации {}", userId, location.getName());
                    messageService.addUserToGroupDialog(userId, location.getDialog().getDialogId());
                }
            });
        }
    }

    @Scheduled(cron = "30 */5 * * * *")
    @Transactional
    @Async
    public void findUsersInLocationAndDeleteDialogUsers() {
        List<Location> locations = locationService.findAll();
        for (Location location : locations) {
            List<Geolocation> geolocations = geolocationService.findInSquare(
                    new Geolocation().setLng(location.getLng()).setLat(location.getLat()),
                    location.getRadius(), LocalDateTime.now(), 5L
            );

            Set<User> users = location.getDialog().getUsers();
            for (User user : users) {
                List<Long> userList = geolocations.stream().map(Geolocation::getUser).map(User::getUserId).toList();
                if (!userList.contains(user.getUserId()) && !user.getUserId().equals(ADMIN_ID)) {
                    log.info("Удаляю пользователя {} из диалога локации {}", user.getUserId(), location.getName());
                    messageService.deleteUserFromGroupDialog(user.getUserId(), location.getDialog().getDialogId());
                }
            }
        }
    }
}
