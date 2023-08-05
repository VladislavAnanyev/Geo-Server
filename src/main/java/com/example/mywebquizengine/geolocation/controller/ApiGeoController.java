package com.example.mywebquizengine.geolocation.controller;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.meeting.model.GeolocationModel;
import com.example.mywebquizengine.meeting.model.GetGeolocationsResponse;
import com.example.mywebquizengine.meeting.model.GetMeetingsResponse;
import com.example.mywebquizengine.meeting.facade.GeolocationFacade;
import com.example.mywebquizengine.meeting.model.dto.input.GeolocationRequest;
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

    @ApiOperation(value = "Получить список встреч за указанную дату")
    @GetMapping(path = "/meetings")
    public GetMeetingsResponse getMeetings(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                           @RequestParam(required = false) String date) {
        return new GetMeetingsResponse(
                geolocationFacade.getMeetings(authUser.getUserId(), date)
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
}
