package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.geo.dto.input.GeolocationRequest;
import com.example.mywebquizengine.model.geo.dto.output.MeetingViewCustomQuery;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.GeoService;
import com.example.mywebquizengine.service.model.GeolocationModel;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api")
public class ApiGeoController {

    private final GeoService geoService;

    public ApiGeoController(GeoService geoService) {
        this.geoService = geoService;
    }

    @GetMapping(path = "/meetings")
    public ArrayList<MeetingViewCustomQuery> getMyMeetings(@ApiIgnore @AuthenticationPrincipal User authUser,
                                                           @RequestParam(required = false) String date) {
        return geoService.getMyMeetings(authUser.getUserId(), date);
    }

    @PostMapping(path = "/sendGeolocation")
    public void sendGeolocation(@ApiIgnore @AuthenticationPrincipal User authUser,
                                @RequestBody GeolocationRequest geolocationRequest) throws Exception {

        GeolocationModel geolocationModel = new GeolocationModel();
        geolocationModel.setLng(geolocationRequest.getLng());
        geolocationModel.setLat(geolocationRequest.getLat());
        geoService.sendGeolocation(authUser.getUserId(), geolocationModel);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "OK";
    }

    @PostMapping(path = "/loadGoogleHistory")
    @ResponseBody
    public void handleGoogleHistoryUpload(@RequestParam("file") MultipartFile file,
                                          @ApiIgnore @AuthenticationPrincipal User authUser) {
        geoService.loadGeolocationHistory(file, authUser.getUserId());
    }

}
