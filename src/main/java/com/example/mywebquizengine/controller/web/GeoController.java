package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.geo.Geolocation;
import com.example.mywebquizengine.model.projection.GeolocationView;
import com.example.mywebquizengine.model.projection.UserCommonView;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.GeoService;
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
                                @RequestBody Geolocation myGeolocation) throws Exception {
        geoService.sendGeolocation(user.getUsername(), myGeolocation);
    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public ArrayList<GeolocationView> getAllGeoWithoutMe(@AuthenticationPrincipal User user) {

        return geoService.getAllGeo(userService.
                loadUserByUsernameProxy(user.getUsername()).getUsername());
    }



    @GetMapping(path = "/square")
    @ResponseBody
    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation,
                                               @RequestParam(required = false, defaultValue = "1000") String size,
                                               String time) {

        return geoService.findInSquare(authUser, myGeolocation, size, time);

    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal User user,
                                @RequestParam(required = false) String date) {


        model.addAttribute("myUsername", user.getUsername());

        List<UserCommonView> friends = userService.findMyFriends(user.getUsername());

        List<String> friendsName = friends.stream().map(UserCommonView::getUsername).collect(Collectors.toList());

        model.addAttribute("friendsName", friendsName);

        model.addAttribute("meetings", geoService.getMyMeetings(user.getUsername(), date));

        return "meetings";
    }

}
