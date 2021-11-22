package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Projection.GeolocationView;
import com.example.mywebquizengine.Model.Projection.UserCommonView;
import com.example.mywebquizengine.Service.GeoService;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
    public void sendGeolocation(@AuthenticationPrincipal Principal principal,
                                @RequestBody Geolocation myGeolocation) throws Exception {
       geoService.sendGeolocation(principal.getName(), myGeolocation);
    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public ArrayList<GeolocationView> getAllGeoWithoutMe(@AuthenticationPrincipal Principal principal) {

        return geoService.getAllGeo(userService.
                loadUserByUsernameProxy(principal.getName()).getUsername());
    }



    @GetMapping(path = "/square")
    @ResponseBody
    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation,
                                               @RequestParam(required = false, defaultValue = "1000") String size,
                                               String time) {

        return geoService.findInSquare(authUser, myGeolocation, size, time);

    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal Principal principal,
                                @RequestParam(required = false) String date) {


        model.addAttribute("myUsername", principal.getName());

        List<UserCommonView> friends = userService.findMyFriends(principal.getName());

        List<String> friendsName = friends.stream().map(UserCommonView::getUsername).collect(Collectors.toList());

        model.addAttribute("friendsName", friendsName);

        model.addAttribute("meetings", geoService.getMyMeetings(principal.getName(), date));

        return "meetings";
    }

}
