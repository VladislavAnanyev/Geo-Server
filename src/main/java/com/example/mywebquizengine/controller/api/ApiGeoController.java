package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.geo.Geolocation;
import com.example.mywebquizengine.model.projection.MeetingViewCustomQuery;
import com.example.mywebquizengine.service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "/api")
public class ApiGeoController {

    @Autowired
    private GeoService geoService;

    @GetMapping(path = "/meetings")
    public ArrayList<MeetingViewCustomQuery> getMyMeetings(@AuthenticationPrincipal Principal principal,
                                                           @RequestParam(required = false) String date) {
        return geoService.getMyMeetings(principal.getName(), date);
    }

    @PostMapping(path = "/sendGeolocation")
    public void sendGeolocation(@AuthenticationPrincipal Principal principal, @RequestBody Geolocation myGeolocation) throws Exception {
        geoService.sendGeolocation(principal.getName(), myGeolocation);
    }

    @GetMapping(path = "/test")
    public String test() {
        return "OK";
    }

    @PostMapping(path = "/loadGoogleHistory")
    @ResponseBody
    public void handleGoogleHistoryUpload(@RequestParam("file") MultipartFile file,
                                            @AuthenticationPrincipal Principal principal) {

        geoService.loadGeolocationHistory(file, principal.getName());
    }

}
