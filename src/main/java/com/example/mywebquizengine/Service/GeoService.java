package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Controller.GeoController;
import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Geo.Meeting;
import com.example.mywebquizengine.Model.Projection.MeetingViewCustomQuery;
import com.example.mywebquizengine.Repos.GeolocationRepository;
import com.example.mywebquizengine.Repos.MeetingRepository;
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
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.security.Principal;
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
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private GeoController geoController;


    public ArrayList<MeetingViewCustomQuery> getMyMeetings(Principal principal, String date) {
        if (date == null) {
            Calendar calendar = new GregorianCalendar();
            Timestamp timestamp = Timestamp.from(calendar.toInstant());

            date = timestamp.toString().substring(0,10);
        }
        return (ArrayList<MeetingViewCustomQuery>) meetingRepository.getMyMeetings(principal.getName(), date);
    }

    public void sendGeolocation(Principal principal, Geolocation myGeolocation) throws JsonProcessingException, ParseException {
        myGeolocation.setUser(userService.loadUserByUsernameProxy(principal.getName()));

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
            System.out.println(time);
            //time = myGeolocation.getTime()
        }


        //geolocation.setId(geolocation.getUser().getUsername());
        userService.saveGeo(myGeolocation);




        //System.out.println(date.toString().substring(0,10));

        ArrayList<Geolocation> peopleNearMe = geoController
                .findInSquare(principal.getName(),myGeolocation, "20", time);

        if (peopleNearMe.size() > 0) {

            for (int i = 0; i < peopleNearMe.size(); i++) {

                if (meetingRepository.
                        getMeetings(myGeolocation.getUser().getUsername(),
                                peopleNearMe.get(i).getUser().getUsername(), time.substring(0,10))
                        .size() == 0 ) {


                    if (!principal.getName().equals(peopleNearMe.get(i).getUser().getUsername())) {

                        Meeting meeting = new Meeting();
                        meeting.setFirstUser(myGeolocation.getUser());
                        meeting.setSecondUser(peopleNearMe.get(i).getUser());
                        meeting.setLat(myGeolocation.getLat());
                        meeting.setLng(myGeolocation.getLng());
                        meeting.setTime(timestamp);

                        meetingRepository.save(meeting);


                        JSONObject jsonObject = (JSONObject) JSONValue.parseWithException(objectMapper
                                .writeValueAsString(meetingRepository.findMeetingById(meeting.getId())));
                        jsonObject.put("type", "MEETING");


                        simpMessagingTemplate.convertAndSend("/topic/" +
                                meeting.getFirstUser().getUsername(), jsonObject);

                        simpMessagingTemplate.convertAndSend("/topic/" +
                                meeting.getSecondUser().getUsername(), jsonObject);

                        rabbitTemplate.setExchange("message-exchange");

                        rabbitTemplate.convertAndSend(meeting.getFirstUser().getUsername(), jsonObject);
                        rabbitTemplate.convertAndSend(meeting.getSecondUser().getUsername(), jsonObject);


                    }

                }

            }
    }
}

    public void loadGeolocationHistory(MultipartFile file, Principal principal) {

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
                /*System.out.println("- lat: " + (Long)test.get("latitudeE7")/1e7 + ", lng: " + (Long)test.get("longitudeE7")/1e7
                + ", time: " + date.toString());*/

                Geolocation geolocation = new Geolocation();
                geolocation.setLat((Long) test.get("latitudeE7") / 1e7);
                geolocation.setLng((Long) test.get("longitudeE7") / 1e7);
                geolocation.setTime(date);

                Example<Geolocation> example = Example.of(geolocation);

                if (!geolocationRepository.exists(example)) {
                    sendGeolocation(principal, geolocation);
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

    public double deg2rad( double degrees) {
        return degrees * Math.PI / 180;
    }

    public ArrayList<Geolocation> findInSquare(String authUser, Geolocation myGeolocation, String size, String time) {

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
    }
}
