package com.example.meetings.geolocation.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.geolocation.facade.GeolocationFacade;
import com.example.meetings.meeting.model.*;
import com.example.meetings.meeting.model.dto.input.GeolocationRequest;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiGeoController {

    @Autowired
    private GeolocationFacade geolocationFacade;

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
}
