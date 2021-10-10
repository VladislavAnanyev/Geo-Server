package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Controller.api.ApiController;
import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Geo.Meeting;
import com.example.mywebquizengine.Model.Projection.UserCommonView;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.GeolocationRepository;
import com.example.mywebquizengine.Repos.MeetingRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private UserController userController;

    @Autowired
    private ApiController apiController;

    @GetMapping("/geo")
    public String geo() {
        return "geo";
    }

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public void sendGeolocation(@AuthenticationPrincipal Principal principal, @RequestBody Geolocation myGeolocation) throws JsonProcessingException, ParseException {
       apiController.sendGeolocation(principal, myGeolocation);
       userController.testConnection(principal);

    }

    @GetMapping(path = "/getAllGeoWithoutMe")
    @ResponseBody
    public ArrayList<Geolocation> getAllGeoWithoutMe(@AuthenticationPrincipal Principal principal) {

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
    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation, @RequestParam(required = false, defaultValue = "1000") String size) {

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
                .findInSquare(myLatitude,myLongitude, aroundLat, aroundLng, userService
                        .loadUserByUsernameProxy(authUser)
                        .getUsername());
        //return aroundLat + " " + aroundLon;
    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal Principal principal) {

        User authUser = userService.loadUserByUsername(principal.getName());

        model.addAttribute("myUsername", authUser.getUsername());
        Calendar calendar = new GregorianCalendar();
        Timestamp date = Timestamp.from(calendar.toInstant());

        List<UserCommonView> friends = userRepository.findUsersByFriendsUsernameContains(authUser.getUsername());



        List<String> friendsName = new ArrayList<>();

        for (int i = 0; i < friends.size(); i++) {
            friendsName.add(friends.get(i).getUsername());
        }

        model.addAttribute("friendsName", friendsName);

        //ArrayList<Geolocation> peopleNearMe = findInSquare(SecurityContextHolder.getContext().getAuthentication(),"20");

        //System.out.println(users.get(0).getUsername());
        model.addAttribute("meetings", meetingRepository.getMyMeetingsToday(authUser.getUsername(),
                date.toString().substring(0,10) + " 00:00:00",
                date.toString().substring(0,10) + " 23:59:59"));
        /*return (ArrayList<Meeting>) meetingRepository.getMyMeetingsToday(userService.getAuthUserNoProxy
                (SecurityContextHolder.getContext().getAuthentication()).getUsername(),
                        date.toString().substring(0,10) + " 00:00:00",
                date.toString().substring(0,10) + " 23:59:59");*/
        return "meetings";
    }

}
