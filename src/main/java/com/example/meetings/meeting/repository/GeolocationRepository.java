package com.example.meetings.meeting.repository;

import com.example.meetings.geolocation.model.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeolocationRepository extends CrudRepository<Geolocation, String>, JpaRepository<Geolocation, String> {

    /**
     * Получить текущие координаты указанных пользователей
     *
     * @param userIds список идентификаторов пользователей
     * @return текущие координаты указанных пользователей
     */
    @Query(value = """
            SELECT *
            FROM USERS JOIN GEOLOCATIONS G ON USERS.USER_ID = G.USER_ID
            WHERE G.USER_ID IN :userIds
            """, nativeQuery = true)
    List<Geolocation> getUsersGeolocations(List<Long> userIds);

    /**
     * Найти пользователей рядом
     *
     * @param myLat
     * @param myLng
     * @param aroundLat
     * @param aroundLng
     * @param userId
     * @param time
     * @return
     */
    @Query(value = """
            SELECT *
            FROM GEOLOCATIONS
            WHERE LAT BETWEEN :myLat - :aroundLat AND :myLat + :aroundLat
              AND LNG BETWEEN :myLng - :aroundLng AND :myLng + :aroundLng
              AND UPDATED_AT BETWEEN CAST(:time AS TIMESTAMP) - INTERVAL '1 minute'
                AND CAST(:time AS TIMESTAMP) + INTERVAL '1 minute' AND user_id != :userId""", nativeQuery = true)
    List<Geolocation> findInSquare(Double myLat, Double myLng, Double aroundLat, Double aroundLng, Long userId, String time);
}

