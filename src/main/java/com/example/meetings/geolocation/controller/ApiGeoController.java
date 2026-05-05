package com.example.meetings.geolocation.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.geolocation.facade.GeolocationFacade;
import com.example.meetings.integration.dadata.DaDataClient;
import com.example.meetings.integration.dadata.SuggestionsResponse;
import com.example.meetings.meeting.model.*;
import com.example.meetings.meeting.model.dto.input.GeolocationRequest;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api/v1")
@Slf4j
public class ApiGeoController {

    @Autowired
    private GeolocationFacade geolocationFacade;

    @Autowired
    private DaDataClient daDataClient;

    @ApiOperation(value = "Проверить, была ли недавняя геолокация")
    @GetMapping(path = "/geolocation/recent")
    public RecentGeolocationResponse getRecentGeolocation(@AuthenticationPrincipal AuthUserDetails authUser) {
        return new RecentGeolocationResponse()
                .setLastGeolocation(
                        geolocationFacade.getRecentOwnGeolocation(authUser.getUserId()).orElse(null)
                );
    }

    @ApiOperation(value = "Пользователи в радиусе от последней геолокации текущего пользователя")
    @GetMapping(path = "/geolocation/people-nearby")
    public GetGeolocationsResponse getPeopleNearby(@AuthenticationPrincipal AuthUserDetails authUser) {
        return new GetGeolocationsResponse(
                new GetGeolocationsResult().setItems(geolocationFacade.getPeopleInSquare(authUser.getUserId()))
        );
    }

    @ApiOperation(value = "Получить информацию о геолокации друзей")
    @GetMapping(path = "/geolocations")
    public GetGeolocationsResponse getFriendsGeolocations(@AuthenticationPrincipal AuthUserDetails authUser) {
        return new GetGeolocationsResponse(
                geolocationFacade.getFriendsGeolocations(authUser.getUserId())
        );
    }

    @ApiOperation(value = "Отправить текущее местоположение")
    @PostMapping(path = "/geolocation")
    public SuccessfulResponse sendGeolocation(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                              @RequestBody GeolocationRequest geolocationRequest) {
        geolocationFacade.addGeolocation(
                authUser.getUserId(),
                new GeolocationModel()
                        .setLng(geolocationRequest.getLng())
                        .setLat(geolocationRequest.getLat())
        );
        return new SuccessfulResponse();
    }

    @GetMapping("/geolocation/suggestions")
    @ApiOperation(value = "Получить подсказки по адресу")
    public SuggestionsResponse getSuggestions(@RequestParam String address) {
        return new SuggestionsResponse()
                .setResult(daDataClient.findSuggestions(address));
    }
}
