package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Geo.Meeting;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.GeolocationRepository;
import com.example.mywebquizengine.Repos.MeetingRepository;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

@Controller
public class GeoController {

    @Autowired
    private UserService userService;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @GetMapping("/geo")
    public String geo() {
        return "geo";
    }

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public void sendGeolocation(@AuthenticationPrincipal Principal principal, @RequestBody Geolocation myGeolocation) {
        myGeolocation.setUser(userService.loadUserByUsername(principal.getName()));
        //geolocation.setId(geolocation.getUser().getUsername());
        userService.saveGeo(myGeolocation);

        Calendar calendar = new GregorianCalendar();
        Timestamp date = Timestamp.from(calendar.toInstant());
        //System.out.println(date.toString().substring(0,10));

        ArrayList<Geolocation> peopleNearMe = findInSquare(principal.getName(),"20");

        if (peopleNearMe.size() > 0) {

            for (int i = 0; i < peopleNearMe.size(); i++) {

                if (meetingRepository.
                        getMeetings(myGeolocation.getUser().getUsername(),
                                peopleNearMe.get(i).getUser().getUsername(),
                                date.toString().substring(0,10) + " 00:00:00",
                                date.toString().substring(0,10) + " 23:59:59")
                        .size() == 0) {

                    Meeting meeting = new Meeting();
                    meeting.setFirstUser(myGeolocation.getUser());
                    meeting.setSecondUser(peopleNearMe.get(i).getUser());
                    meeting.setLat(myGeolocation.getLat());
                    meeting.setLng(myGeolocation.getLng());
                    meeting.setTime(calendar);

                    meetingRepository.save(meeting);
                }

            }


        }


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
    public ArrayList<Geolocation> findInSquare(String authUser, @RequestParam(required = false, defaultValue = "1000") String size) {

        int DISTANCE = Integer.parseInt(size); // Интересующее нас расстояние

        double myLatitude = geolocationRepository.findById("application").get().getLat(); //Интересующие нас координаты широты
        double myLongitude = geolocationRepository.findById("application").get().getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = DISTANCE / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = DISTANCE / deltaLon; // Вычисляем диапазон координат по долготе

        //System.out.println(aroundLat + " " + aroundLng);
        return (ArrayList<Geolocation>) geolocationRepository.findInSquare(myLatitude,myLongitude, aroundLat, aroundLng, userService.loadUserByUsernameProxy(authUser).getUsername());
        //return aroundLat + " " + aroundLon;
    }


    @GetMapping(path = "/meetings")
    public String getMyMeetings(Model model, @AuthenticationPrincipal Principal principal) {

        User authUser = userService.loadUserByUsername(principal.getName());

        model.addAttribute("myUsername", authUser.getUsername());
        Calendar calendar = new GregorianCalendar();
        Timestamp date = Timestamp.from(calendar.toInstant());

        //ArrayList<Geolocation> peopleNearMe = findInSquare(SecurityContextHolder.getContext().getAuthentication(),"20");

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
