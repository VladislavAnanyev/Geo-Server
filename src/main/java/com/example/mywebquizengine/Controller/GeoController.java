package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Controller.api.ApiGeoController;
import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Projection.GeolocationView;
import com.example.mywebquizengine.Model.Projection.UserCommonView;
import com.example.mywebquizengine.Repos.GeolocationRepository;
import com.example.mywebquizengine.Repos.MeetingRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.parser.ParseException;
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
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private ApiGeoController apiGeoController;

    @GetMapping("/geo")
    public String geo() {
        return "geo";
    }

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public void sendGeolocation(@AuthenticationPrincipal Principal principal, @RequestBody Geolocation myGeolocation) throws Exception {
       apiGeoController.sendGeolocation(principal, myGeolocation);
       //userController.testConnection(/*principal*/);

    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public ArrayList<GeolocationView> getAllGeoWithoutMe(@AuthenticationPrincipal Principal principal) {

        return userService.getAllGeo(userService.
                loadUserByUsernameProxy(principal.getName()).getUsername());
    }

    //https://en.wikipedia.org/wiki/Longitude#Length_of_a_degree_of_longitude
    public double computeDelta(double degrees) {
        int EARTH_RADIUS = 6371210; //Радиус земли
        return Math.PI / 180 * EARTH_RADIUS * Math.cos(deg2rad(degrees));
    }

    public double deg2rad( double degrees) {
        return degrees * Math.PI / 180;
    }

    @GetMapping(path = "/square")
    @ResponseBody
    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation,
                                               @RequestParam(required = false, defaultValue = "1000") String size,
                                               String time) {

        int DISTANCE = Integer.parseInt(size); // Интересующее нас расстояние

        //Geolocation myGeolocation = geolocationRepository.findById(authUser).get();
        //Timestamp date = Timestamp.from(myGeolocation.getTime().toInstant());


        double myLatitude = myGeolocation.getLat(); //Интересующие нас координаты широты
        double myLongitude = myGeolocation.getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = DISTANCE / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = DISTANCE / deltaLon; // Вычисляем диапазон координат по долготе

        //System.out.println(aroundLat + " " + aroundLng);




            return (ArrayList<Geolocation>) geolocationRepository
                    .findInSquare(myLatitude, myLongitude, aroundLat, aroundLng, userService
                            .loadUserByUsernameProxy(authUser)
                            .getUsername(), time);

        //return aroundLat + " " + aroundLon;
    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal Principal principal,
                                @RequestParam(required = false) String date) {


        model.addAttribute("myUsername", principal.getName());

        List<UserCommonView> friends = userRepository.findUsersByFriendsUsernameContains(principal.getName());

        List<String> friendsName = friends.stream().map(UserCommonView::getUsername).collect(Collectors.toList());

        model.addAttribute("friendsName", friendsName);

        model.addAttribute("meetings", apiGeoController.getMyMeetings(principal, date));

        return "meetings";
    }

}
