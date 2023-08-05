package com.example.mywebquizengine.geolocation.service;

import com.example.mywebquizengine.geolocation.model.Geolocation;
import com.example.mywebquizengine.geolocation.model.GeolocationHistory;
import com.example.mywebquizengine.geolocation.repository.GeolocationHistoryRepository;
import com.example.mywebquizengine.meeting.model.GeolocationModel;
import com.example.mywebquizengine.meeting.repository.GeolocationRepository;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class GeolocationService {

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private GeolocationHistoryRepository geolocationHistoryRepository;

    @Autowired
    private UserService userService;

    /**
     * Получить координаты друзей пользователя
     *
     * @param userId идентификатор пользователя
     * @return координаты друзей пользователя
     */
    public List<Geolocation> getFriendsGeolocations(Long userId) {
        User user = userService.loadUserByUserId(userId);
        List<Long> friendsIds = user.getFriends()
                .stream()
                .map(User::getUserId)
                .collect(toList());

        return geolocationRepository.getUsersGeolocations(friendsIds);
    }

    public Geolocation saveGeolocation(Long userId, GeolocationModel geolocationModel) {
        LocalDateTime now = LocalDateTime.now();

        geolocationHistoryRepository.save(
                new GeolocationHistory()
                        .setUser(userService.loadUserByUserId(userId))
                        .setLat(geolocationModel.getLat())
                        .setLng(geolocationModel.getLng())
                        .setCreatedAt(now)
        );

        return geolocationRepository.save(
                new Geolocation()
                        .setUser(userService.loadUserByUserId(userId))
                        .setLat(geolocationModel.getLat())
                        .setLng(geolocationModel.getLng())
                        .setUpdatedAt(now)
        );
    }

    public List<Geolocation> findInSquare(Long authUserId, Geolocation myGeolocation, Integer size, String time) {
        double myLatitude = myGeolocation.getLat(); //Интересующие нас координаты широты
        double myLongitude = myGeolocation.getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = size / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = size / deltaLon; // Вычисляем диапазон координат по долготе

        return geolocationRepository.findInSquare(
                myLatitude, myLongitude, aroundLat,
                aroundLng, userService.loadUserByUserIdProxy(authUserId).getUserId(), time
        );
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
