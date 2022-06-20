package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.geo.domain.Geolocation;
import com.example.mywebquizengine.model.geo.domain.Meeting;
import com.example.mywebquizengine.model.geo.dto.output.GeolocationView;
import com.example.mywebquizengine.model.geo.dto.output.MeetingView;
import com.example.mywebquizengine.model.geo.dto.output.MeetingViewCustomQuery;
import com.example.mywebquizengine.model.rabbit.MeetingType;
import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.repos.GeolocationRepository;
import com.example.mywebquizengine.repos.MeetingRepository;
import com.example.mywebquizengine.service.utils.ProjectionUtil;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.util.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.sql.Timestamp;
import java.util.*;

@Service
public class GeoService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private ProjectionUtil projectionUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    public ArrayList<MeetingViewCustomQuery> getMyMeetings(Long userId, String date) {
        if (date == null) {
            Calendar calendar = new GregorianCalendar();
            Timestamp timestamp = Timestamp.from(calendar.toInstant());

            date = timestamp.toString().substring(0, 10);
        }
        return (ArrayList<MeetingViewCustomQuery>) meetingRepository.getMyMeetings(userId, date);
    }

    public void sendGeolocation(Long userId, GeolocationModel geolocationModel) throws JsonProcessingException {
        Geolocation geolocation = new Geolocation();
        geolocation.setUser(userService.loadUserByUserId(userId));
        geolocation.setLat(geolocationModel.getLat());
        geolocation.setLng(geolocationModel.getLng());

        String time = "";
        Timestamp timestamp;
        if (geolocation.getTime() == null) {
            timestamp = Timestamp.from(new GregorianCalendar().toInstant());
            geolocation.setTime(timestamp);
            time = timestamp.toString();
        } else {
            timestamp = Timestamp.from(geolocation.getTime().toInstant());
            time = timestamp.toString();
        }

        geolocationRepository.save(geolocation);
        ArrayList<Geolocation> peopleNearMe = findInSquare(userId, geolocation, "20", time);

        if (peopleNearMe.size() > 0) {

            for (Geolocation value : peopleNearMe) {

                List<Meeting> meetings = meetingRepository.getMeetings(geolocation.getUser().getUserId(),
                        value.getUser().getUserId(),
                        time.substring(0, 10)
                );

                if (meetings.size() == 0) {

                    if (!userId.equals(value.getUser().getUserId())) {

                        Meeting meeting = new Meeting();
                        meeting.setFirstUser(geolocation.getUser());
                        meeting.setSecondUser(value.getUser());
                        meeting.setLat(geolocation.getLat());
                        meeting.setLng(geolocation.getLng());
                        meeting.setTime(timestamp);

                        meetingRepository.save(meeting);


                        /*
                         * Так как первый пользователь это аутентифированный пользователь, то
                         * ему возвращается второй пользователь в качестве user в встрече, а
                         * второму пользователю возвращается первый. Это достигается при помощи
                         * одной модели, путем смены местами первого и второго пользователя
                         * */

                        MeetingView meetingViewForFirstUser = projectionUtil
                                .parseToProjection(
                                        meeting,
                                        MeetingView.class
                                );

                        RealTimeEvent<MeetingView> realTimeEventForFirstUser = new RealTimeEvent<>();
                        realTimeEventForFirstUser.setType(MeetingType.MEETING);
                        realTimeEventForFirstUser.setPayload(meetingViewForFirstUser);


                        User initialFirstUser = meeting.getFirstUser();
                        User initialSecondUser = meeting.getSecondUser();

                        String firstUserExchangeName = RabbitUtil.getExchangeName(initialFirstUser.getUserId());

                        rabbitTemplate.convertAndSend(firstUserExchangeName, "",
                                JSONValue.parse(objectMapper.writeValueAsString(realTimeEventForFirstUser)));

                        meeting.setFirstUser(meeting.getSecondUser());
                        meeting.setSecondUser(initialFirstUser);

                        MeetingView meetingViewForSecondUser = projectionUtil
                                .parseToProjection(
                                        meeting,
                                        MeetingView.class
                                );

                        RealTimeEvent<MeetingView> realTimeEventForSecondUser = new RealTimeEvent<>();
                        realTimeEventForSecondUser.setType(MeetingType.MEETING);
                        realTimeEventForSecondUser.setPayload(meetingViewForSecondUser);


                        String secondUserExchangeName = RabbitUtil.getExchangeName(initialSecondUser.getUserId());

                        rabbitTemplate.convertAndSend(secondUserExchangeName, "",
                                JSONValue.parse(objectMapper.writeValueAsString(realTimeEventForSecondUser)));

                    }
                }
            }
        }
    }

    public void loadGeolocationHistory(MultipartFile file, Long userId) {

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

                    GeolocationModel geolocationModel = new GeolocationModel();
                    geolocationModel.setLat(geolocation.getLat());
                    geolocationModel.setLng(geolocationModel.getLng());
                    geolocationModel.setTime(geolocation.getTime());

                    sendGeolocation(userId, geolocationModel);
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

    public ArrayList<Geolocation> findInSquare(Long authUserId, Geolocation myGeolocation, String size, String time) {

        int DISTANCE = Integer.parseInt(size); // Интересующее нас расстояние

        double myLatitude = myGeolocation.getLat(); //Интересующие нас координаты широты
        double myLongitude = myGeolocation.getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = DISTANCE / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = DISTANCE / deltaLon; // Вычисляем диапазон координат по долготе

        return (ArrayList<Geolocation>) geolocationRepository
                .findInSquare(myLatitude, myLongitude, aroundLat, aroundLng, userService
                        .loadUserByUserIdProxy(authUserId)
                        .getUserId(), time);
    }

    public ArrayList<GeolocationView> getAllGeo(Long userId) {
        return (ArrayList<GeolocationView>) geolocationRepository.getAll(userId);
    }


}
