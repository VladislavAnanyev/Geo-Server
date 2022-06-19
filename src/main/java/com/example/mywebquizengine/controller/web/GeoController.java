package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.geo.domain.Geolocation;
import com.example.mywebquizengine.model.geo.dto.input.GeolocationRequest;
import com.example.mywebquizengine.model.geo.dto.output.GeolocationView;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.GeoService;
import com.example.mywebquizengine.service.GeolocationModel;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GeoController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeoService geoService;

    @GetMapping("/geo")
    public String geo() {
        return "geo";
    }

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public void sendGeolocation(@AuthenticationPrincipal User user,
                                @RequestBody GeolocationRequest geolocationRequest) throws Exception {
        GeolocationModel geolocationModel = new GeolocationModel();
        geolocationModel.setLng(geolocationRequest.getLng());
        geolocationModel.setLat(geolocationRequest.getLat());
        geoService.sendGeolocation(user.getUserId(), geolocationModel);
    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public ArrayList<GeolocationView> getAllGeoWithoutMe(@AuthenticationPrincipal User user) {
        return geoService.getAllGeo(userService.loadUserByUserIdProxy(user.getUserId()).getUserId());
    }


    @GetMapping(path = "/square")
    @ResponseBody
    public ArrayList<Geolocation> findInSquare(@AuthenticationPrincipal User authUser, Geolocation myGeolocation,
                                               @RequestParam(required = false, defaultValue = "1000") String size,
                                               String time) {
        return geoService.findInSquare(authUser.getUserId(), myGeolocation, size, time);
    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal User user, @RequestParam(required = false) String date) {
        model.addAttribute("myUsername", user.getUsername());
        List<UserCommonView> friends = userService.findMyFriends(user.getUserId());
        List<Long> friendsName = friends.stream().map(UserCommonView::getUserId).collect(Collectors.toList());
        model.addAttribute("friendsName", friendsName);
        model.addAttribute("meetings", geoService.getMyMeetings(user.getUserId(), date));
        return "meetings";
    }

}
