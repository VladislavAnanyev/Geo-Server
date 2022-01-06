package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.model.geo.Geolocation;
import com.example.mywebquizengine.model.geo.Meeting;
import com.example.mywebquizengine.model.projection.GeolocationView;
import com.example.mywebquizengine.model.projection.MeetingView;
import com.example.mywebquizengine.model.projection.MeetingViewCustomQuery;
import com.example.mywebquizengine.model.rabbit.MeetingType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.rabbit.RequestType;
import com.example.mywebquizengine.repos.GeolocationRepository;
import com.example.mywebquizengine.repos.MeetingRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

@Service
public class GeoService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final JsonParser jsonParser = new BasicJsonParser();


    public ArrayList<MeetingViewCustomQuery> getMyMeetings(String username, String date) {
        if (date == null) {
            Calendar calendar = new GregorianCalendar();
            Timestamp timestamp = Timestamp.from(calendar.toInstant());

            date = timestamp.toString().substring(0, 10);
        }
        return (ArrayList<MeetingViewCustomQuery>) meetingRepository.getMyMeetings(username, date);
    }

    public void sendGeolocation(String username, Geolocation myGeolocation) throws JsonProcessingException, ParseException {
        myGeolocation.setUser(userService.loadUserByUsername(username));

        String time = "";
        Timestamp timestamp;
        if (myGeolocation.getTime() == null) {
            timestamp = Timestamp.from(new GregorianCalendar().toInstant());
            myGeolocation.setTime(timestamp);
            time = timestamp.toString();
            System.out.println(time);
        } else {
            timestamp = Timestamp.from(myGeolocation.getTime().toInstant());
            time = timestamp.toString();
        }

        geolocationRepository.save(myGeolocation);

        ArrayList<Geolocation> peopleNearMe = findInSquare(username, myGeolocation, "20", time);

        if (peopleNearMe.size() > 0) {

            for (int i = 0; i < peopleNearMe.size(); i++) {

                if (meetingRepository.
                        getMeetings(myGeolocation.getUser().getUsername(),
                                peopleNearMe.get(i).getUser().getUsername(), time.substring(0, 10))
                        .size() == 0) {

                    if (!username.equals(peopleNearMe.get(i).getUser().getUsername())) {

                        Meeting meeting = new Meeting();
                        meeting.setFirstUser(myGeolocation.getUser());
                        meeting.setSecondUser(peopleNearMe.get(i).getUser());
                        meeting.setLat(myGeolocation.getLat());
                        meeting.setLng(myGeolocation.getLng());
                        meeting.setTime(timestamp);

                        meetingRepository.save(meeting);


                        /*
                         * Так как первый пользователь это аутентифированный пользователь, то
                         * ему возвращается второй пользователь в качестве user в встрече, а
                         * второму пользователю возвращается первый. Это достигается при помощи
                         * одной модели, путем смены местами первого и второго пользователя
                         * */

                        MeetingView meetingViewForFirstUser = ProjectionUtil
                                .parseToProjection(
                                        meeting,
                                        MeetingView.class
                                );

                        RabbitMessage<MeetingView> rabbitMessageForFirstUser = new RabbitMessage<>();
                        rabbitMessageForFirstUser.setType(MeetingType.MEETING);
                        rabbitMessageForFirstUser.setPayload(meetingViewForFirstUser);


                        User initialFirstUser = meeting.getFirstUser();
                        User initialSecondUser = meeting.getSecondUser();

                        rabbitTemplate.convertAndSend(initialFirstUser.getUsername(), "",
                                jsonParser.parseMap(objectMapper.writeValueAsString(rabbitMessageForFirstUser)));

                        meeting.setFirstUser(meeting.getSecondUser());
                        meeting.setSecondUser(initialFirstUser);

                        MeetingView meetingViewForSecondUser = ProjectionUtil
                                .parseToProjection(
                                        meeting,
                                        MeetingView.class
                                );

                        RabbitMessage<MeetingView> rabbitMessageForSecondUser = new RabbitMessage<>();
                        rabbitMessageForSecondUser.setType(MeetingType.MEETING);
                        rabbitMessageForSecondUser.setPayload(meetingViewForSecondUser);


                        rabbitTemplate.convertAndSend(initialSecondUser.getUsername(), "",
                                jsonParser.parseMap(objectMapper.writeValueAsString(rabbitMessageForSecondUser)));

                    }
                }
            }
        }
    }

    public void loadGeolocationHistory(MultipartFile file, String username) {

        try {

            ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
            String myString = IOUtils.toString(stream, Charset.defaultCharset());

            Object obj = new JSONParser().parse(myString);

            JSONObject jo = (JSONObject) obj;

            JSONArray phoneNumbersArr = (JSONArray) jo.get("locations");

            for (Object o : phoneNumbersArr) {
                Date date = new Date();

                JSONObject test = (JSONObject) o;

                Long time = Long.parseLong((String) test.get("timestampMs"));

                date.setTime(time);

                Geolocation geolocation = new Geolocation();
                geolocation.setLat((Long) test.get("latitudeE7") / 1e7);
                geolocation.setLng((Long) test.get("longitudeE7") / 1e7);
                geolocation.setTime(date);

                Example<Geolocation> example = Example.of(geolocation);

                if (!geolocationRepository.exists(example)) {
                    sendGeolocation(username, geolocation);
                }


            }

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    //https://en.wikipedia.org/wiki/Longitude#Length_of_a_degree_of_longitude
    public double computeDelta(double degrees) {
        int EARTH_RADIUS = 6371210; //Радиус земли
        return Math.PI / 180 * EARTH_RADIUS * Math.cos(deg2rad(degrees));
    }

    public double deg2rad(double degrees) {
        return degrees * Math.PI / 180;
    }

    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation, String size, String time) {

        int DISTANCE = Integer.parseInt(size); // Интересующее нас расстояние

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
    }

    public ArrayList<GeolocationView> getAllGeo(String username) {
        return (ArrayList<GeolocationView>) geolocationRepository.getAll(username);
    }


}
