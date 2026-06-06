package com.example.meetings.geolocation.service;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.model.GeolocationHistory;
import com.example.meetings.geolocation.repository.GeolocationHistoryRepository;
import com.example.meetings.geolocation.sharing.service.LocationSharingService;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.geolocation.repository.GeolocationRepository;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class GeolocationService {

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Autowired
    private GeolocationHistoryRepository geolocationHistoryRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationSharingService locationSharingService;

    /**
     * Получить координаты друзей с активным согласием на обмен геолокацией.
     *
     * @param userId идентификатор пользователя
     * @return координаты друзей с активным согласием на обмен
     */
    public List<Geolocation> getFriendsGeolocations(Long userId) {
        List<Long> partnerIds = locationSharingService.findActivePartnerIds(userId);
        if (partnerIds.isEmpty()) {
            return List.of();
        }
        return geolocationRepository.getUsersGeolocations(partnerIds);
    }

    /**
     * Последняя геолокация пользователя, если она не старше заданного окна (включительно по нижней границе времени).
     */
    public Optional<Geolocation> getRecentGeolocation(Long userId, int windowMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(windowMinutes);
        return geolocationRepository.findLatestByUserId(userId)
                .filter(g -> g.getUpdatedAt() != null && !g.getUpdatedAt().isBefore(threshold));
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

    public List<Geolocation> findInSquare(Geolocation geolocation, Integer size, LocalDateTime time) {
        return findInSquare(geolocation, size, time, 30L);
    }

    public List<Geolocation> findInSquare(Geolocation geolocation, Integer size, LocalDateTime time, Long interval) {
        double myLatitude = geolocation.getLat(); //Интересующие нас координаты широты
        double myLongitude = geolocation.getLng();  //Интересующие нас координаты долготы

        double deltaLat = computeDelta(myLatitude); //Получаем дельту по широте
        double deltaLon = computeDelta(myLongitude); // Дельту по долготе

        double aroundLat = size / deltaLat; // Вычисляем диапазон координат по широте
        double aroundLng = size / deltaLon; // Вычисляем диапазон координат по долготе

        return geolocationRepository.findInSquare(
                myLatitude, myLongitude, aroundLat,
                aroundLng, time.minusMinutes(interval), time.plusMinutes(interval)
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
