package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.meeting.GeolocationModel;
import com.example.mywebquizengine.meeting.facade.GeolocationFacade;
import com.example.mywebquizengine.meeting.model.dto.input.GeolocationRequest;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api/v1")
public class ApiGeoController {

    @Autowired
    private GeolocationFacade geolocationFacade;

    @GetMapping(path = "/meetings")
    public List<MeetingView> getMyMeetings(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                           @RequestParam(required = false) String date) {
        return geolocationFacade.getMeetings(authUser.getUserId(), date);
    }

    @PostMapping(path = "/geolocation")
    public void sendGeolocation(@ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser,
                                @RequestBody GeolocationRequest geolocationRequest) {
        geolocationFacade.addGeolocation(
                authUser.getUserId(),
                new GeolocationModel()
                        .setLng(geolocationRequest.getLng())
                        .setLat(geolocationRequest.getLat())
        );
    }

    @PostMapping(path = "/loadGoogleHistory")
    @ResponseBody
    public void handleGoogleHistoryUpload(@RequestParam("file") MultipartFile file,
                                          @ApiIgnore @AuthenticationPrincipal AuthUserDetails authUser) {
        geolocationFacade.addGeolocationHistory(file, authUser.getUserId());
    }
}
