package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.meeting.model.domain.Geolocation;
import com.example.mywebquizengine.meeting.model.dto.input.GeolocationRequest;
import com.example.mywebquizengine.meeting.model.dto.output.GeolocationView;
import com.example.mywebquizengine.meeting.facade.GeolocationFacade;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.model.dto.UserCommonView;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class GeoController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeolocationFacade geolocationFacade;

    @GetMapping("/geo")
    public String geo() {
        return "geo";
    }

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public void sendGeolocation(/*@AuthenticationPrincipal AuthUserDetails user,*/
                                @RequestBody GeolocationRequest geolocationRequest) {
        /*GeolocationModel geolocationModel = new GeolocationModel();
        geolocationModel.setLng(geolocationRequest.getLng());
        geolocationModel.setLat(geolocationRequest.getLat());
        geolocationFacade.processGeolocation(user.getUserId(), geolocationModel);*/
    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public List<GeolocationView> getAllGeoWithoutMe(@AuthenticationPrincipal AuthUserDetails user) {
        return geolocationFacade.getAllUsersGeoNow(userService.loadUserByUserIdProxy(user.getUserId()).getUserId());
    }

    @GetMapping(path = "/square")
    @ResponseBody
    public List<Geolocation> findInSquare(@AuthenticationPrincipal AuthUserDetails authUser, Geolocation myGeolocation,
                                          @RequestParam(required = false, defaultValue = "1000") String size,
                                          String time) {
        return geolocationFacade.getPeopleInSquare(
                authUser.getUserId(),
                myGeolocation,
                Integer.parseInt(size),
                time
        );
    }

    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal AuthUserDetails user, @RequestParam(required = false) String date) {
        model.addAttribute("myUsername", user.getUsername());
        List<UserCommonView> friends = userService.findMyFriends(user.getUserId());
        List<Long> friendsName = friends.stream().map(UserCommonView::getUserId).collect(Collectors.toList());
        model.addAttribute("friendsName", friendsName);
        model.addAttribute("meetings", geolocationFacade.getMeetings(user.getUserId(), date));
        return "meetings";
    }

}
