package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.geo.Geolocation;
import com.example.mywebquizengine.model.geo.GeolocationRequest;
import com.example.mywebquizengine.model.projection.MeetingViewCustomQuery;
import com.example.mywebquizengine.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import java.security.Principal;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api")
public class ApiGeoController {

    @Autowired
    private GeoService geoService;

    @GetMapping(path = "/meetings")
    public ArrayList<MeetingViewCustomQuery> getMyMeetings(@ApiIgnore @AuthenticationPrincipal Principal principal,
                                                           @RequestParam(required = false) String date) {
        return geoService.getMyMeetings(principal.getName(), date);
    }

    @PostMapping(path = "/sendGeolocation")
    public void sendGeolocation(@ApiIgnore @AuthenticationPrincipal Principal principal,
                                @RequestBody GeolocationRequest geolocationRequest) throws Exception {

        Geolocation geolocation = new Geolocation();
        geolocation.setLat(geolocationRequest.getLat());
        geolocation.setLng(geolocationRequest.getLng());

        geoService.sendGeolocation(principal.getName(), geolocation);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "OK";
    }

    @PostMapping(path = "/loadGoogleHistory")
    @ResponseBody
    public void handleGoogleHistoryUpload(@RequestParam("file") MultipartFile file,
                                          @ApiIgnore @AuthenticationPrincipal Principal principal) {
        geoService.loadGeolocationHistory(file, principal.getName());
    }

}
