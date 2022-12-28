package com.example.mywebquizengine.meeting.service;

import com.example.mywebquizengine.meeting.model.GeolocationModel;
import com.example.mywebquizengine.meeting.model.domain.Geolocation;
import com.example.mywebquizengine.meeting.model.domain.Meeting;
import com.example.mywebquizengine.meeting.model.dto.output.GeolocationView;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import com.example.mywebquizengine.meeting.repository.GeolocationRepository;
import com.example.mywebquizengine.meeting.repository.MeetingRepository;
import com.example.mywebquizengine.user.service.UserService;
import io.micrometer.core.instrument.util.IOUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;

@Service
@Slf4j
@AllArgsConstructor
public class GeoService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private UserService userService;

    public ArrayList<MeetingView> getMyMeetings(Long userId, String date) {
        if (date == null) {
            Calendar calendar = new GregorianCalendar();
            Timestamp timestamp = Timestamp.from(calendar.toInstant());
            date = timestamp.toString().substring(0, 10);
        }
        return (ArrayList<MeetingView>) meetingRepository.getMyMeetings(userId, date);
    }

    public Geolocation saveGeolocation(Long userId, GeolocationModel geolocationModel) {
        Geolocation geolocation = new Geolocation();
        geolocation.setUser(userService.loadUserByUserId(userId));
        geolocation.setLat(geolocationModel.getLat());
        geolocation.setLng(geolocationModel.getLng());

        if (geolocationModel.getTime() == null) {
            geolocation.setTime(
                    Timestamp.from(new GregorianCalendar().toInstant())
            );
        } else {
            geolocation.setTime(geolocationModel.getTime());
        }

        return geolocationRepository.save(geolocation);
    }

    public List<Meeting> findMeetings(Geolocation geolocation) {
        String time = geolocation.getTime().toString();

        ArrayList<Geolocation> peopleNearMe = findInSquare(
                geolocation.getUser().getUserId(),
                geolocation,
                20,
                time
        );

        List<Meeting> newMeetings = new ArrayList<>();
        if (peopleNearMe.size() == 0) {
            return newMeetings;
        }

        for (Geolocation peopleGeolocation : peopleNearMe) {
            List<Meeting> meetings = meetingRepository.getMeetings(geolocation.getUser().getUserId(),
                    peopleGeolocation.getUser().getUserId(),
                    time.substring(0, 10)
            );

            if (meetings.size() == 0) {
                if (!geolocation.getUser().getUserId().equals(peopleGeolocation.getUser().getUserId())) {
                    Meeting meeting = new Meeting();
                    meeting.setFirstUser(geolocation.getUser());
                    meeting.setSecondUser(peopleGeolocation.getUser());
                    meeting.setLat(geolocation.getLat());
                    meeting.setLng(geolocation.getLng());
                    meeting.setTime(geolocation.getTime());

                    meetingRepository.save(meeting);
                    newMeetings.add(meeting);
                }
            }
        }
        return newMeetings;
    }

    public void loadGeolocationHistory(MultipartFile file, Long userId) {
        ByteArrayInputStream stream = null;
        try {
            stream = new ByteArrayInputStream(file.getBytes());
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        String myString = IOUtils.toString(stream, Charset.defaultCharset());

        Object obj = null;
        try {
            obj = new JSONParser().parse(myString);
        } catch (ParseException e) {
            log.error(e.getMessage());
        }

        JSONObject jo = (JSONObject) obj;
        JSONArray locations = (JSONArray) jo.get("locations");

        for (Object location : locations) {
            Date date = new Date();
            JSONObject test = (JSONObject) location;

            date.setTime(Long.parseLong((String) test.get("timestampMs")));

            Geolocation geolocation = new Geolocation();
            geolocation.setLat((Long) test.get("latitudeE7") / 1e7);
            geolocation.setLng((Long) test.get("longitudeE7") / 1e7);
            geolocation.setTime(date);

            Example<Geolocation> example = Example.of(geolocation);

            if (!geolocationRepository.exists(example)) {
                GeolocationModel geolocationModel = new GeolocationModel();
                geolocationModel.setLat(geolocation.getLat());
                geolocationModel.setLng(geolocationModel.getLng());
                geolocationModel.setTime(geolocation.getTime());

                saveGeolocation(userId, geolocationModel);
            }
        }
    }

    public ArrayList<Geolocation> findInSquare(Long authUserId, Geolocation myGeolocation, Integer size, String time) {
        double myLatitude = myGeolocation.getLat(); //Интересующие нас координаты широты
        double myLongitude = myGeolocation.getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = size / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = size / deltaLon; // Вычисляем диапазон координат по долготе

        return (ArrayList<Geolocation>) geolocationRepository.findInSquare(
                myLatitude,
                myLongitude,
                aroundLat,
                aroundLng,
                userService.loadUserByUserIdProxy(authUserId).getUserId(),
                time
        );
    }

    public ArrayList<GeolocationView> getAllUsersGeo(Long userId) {
        return (ArrayList<GeolocationView>) geolocationRepository.getAllUserLastGeolocation(userId);
    }

    //https://en.wikipedia.org/wiki/Longitude#Length_of_a_degree_of_longitude
    private double computeDelta(double degrees) {
        int EARTH_RADIUS = 6371210; //Радиус земли
        return Math.PI / 180 * EARTH_RADIUS * Math.cos(deg2rad(degrees));
    }

    private double deg2rad(double degrees) {
        return degrees * Math.PI / 180;
    }
}
