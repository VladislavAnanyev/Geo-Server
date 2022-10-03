package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.meeting.model.dto.input.GeolocationRequest;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import com.example.mywebquizengine.meeting.facade.GeolocationFacade;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.meeting.GeolocationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiGeoController {

    @Autowired
    private GeolocationFacade geolocationFacade;

    @GetMapping(path = "/meetings")
    public List<MeetingView> getMyMeetings(@ApiIgnore @AuthenticationPrincipal User authUser,
                                           @RequestParam(required = false) String date) {
        return geolocationFacade.getMeetings(authUser.getUserId(), date);
    }

    @PostMapping(path = "/sendGeolocation")
    public void sendGeolocation(@ApiIgnore @AuthenticationPrincipal User authUser,
                                @RequestBody GeolocationRequest geolocationRequest) {
        GeolocationModel geolocationModel = new GeolocationModel();
        geolocationModel.setLng(geolocationRequest.getLng());
        geolocationModel.setLat(geolocationRequest.getLat());
        geolocationFacade.processGeolocation(authUser.getUserId(), geolocationModel);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "OK";
    }

    @PostMapping(path = "/loadGoogleHistory")
    @ResponseBody
    public void handleGoogleHistoryUpload(@RequestParam("file") MultipartFile file,
                                          @ApiIgnore @AuthenticationPrincipal User authUser) {
        geolocationFacade.addGeolocationHistory(file, authUser.getUserId());
    }
}
