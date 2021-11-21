package com.example.mywebquizengine.Controller.api;

import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Projection.MeetingViewCustomQuery;
import com.example.mywebquizengine.Service.GeoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
