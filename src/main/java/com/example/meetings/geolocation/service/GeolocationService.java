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

/**
 * Сохранение и поиск геолокаций пользователей, в том числе друзей с активным обменом.
 */
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
     * Возвращает последнюю геолокацию пользователя, если она не старше заданного окна.
     *
     * @param userId         идентификатор пользователя
     * @param windowMinutes максимальный возраст координат в минутах
     * @return актуальная геолокация или пустой результат
     */
    public Optional<Geolocation> getRecentGeolocation(Long userId, int windowMinutes) {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(windowMinutes);
        return geolocationRepository.findLatestByUserId(userId)
                .filter(g -> g.getUpdatedAt() != null && !g.getUpdatedAt().isBefore(threshold));
    }

    /**
     * Сохраняет текущую геолокацию пользователя и запись в историю.
     *
     * @param userId            идентификатор пользователя
     * @param geolocationModel  координаты
     * @return сохранённая геолокация
     */
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

    /**
     * Ищет геолокации в квадратной области вокруг точки с интервалом ±30 минут.
     *
     * @param geolocation опорная точка
     * @param size        половина стороны квадрата в метрах
     * @param time        центральное время поиска
     * @return список найденных геолокаций
     */
    public List<Geolocation> findInSquare(Geolocation geolocation, Integer size, LocalDateTime time) {
        return findInSquare(geolocation, size, time, 30L);
    }

    /**
     * Ищет геолокации в квадратной области вокруг точки в заданном временном окне.
     *
     * @param geolocation опорная точка
     * @param size        половина стороны квадрата в метрах
     * @param time        центральное время поиска
     * @param interval    полуширина временного окна в минутах
     * @return список найденных геолокаций
     */
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
