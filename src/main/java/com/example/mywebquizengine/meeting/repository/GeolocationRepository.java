package com.example.mywebquizengine.meeting.repository;

import com.example.mywebquizengine.meeting.model.domain.Geolocation;
import com.example.mywebquizengine.meeting.model.dto.output.GeolocationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeolocationRepository extends CrudRepository<Geolocation, String>, JpaRepository<Geolocation, String> {
    @Query(value = """
            SELECT *
            FROM GEOLOCATIONS
            WHERE TIME IN (
                SELECT MAX(TIME)
                FROM GEOLOCATIONS
                WHERE user_id != :userId GROUP BY USER_ID)
            """, nativeQuery = true)
    List<GeolocationView> getAllUserLastGeolocation(Long userId);

    @Query(value = """
            SELECT *
            FROM (SELECT * FROM GEOLOCATIONS WHERE TIME IN (SELECT MAX(TIME)
                                                            FROM GEOLOCATIONS
                                                            WHERE TIME <= CAST(:time AS TIMESTAMP) + INTERVAL '1 minute'
                                                            GROUP BY USER_ID)) AS T
            WHERE LAT BETWEEN :myLat - :aroundLat AND :myLat + :aroundLat
              AND LNG BETWEEN :myLng - :aroundLng AND :myLng + :aroundLng
              AND TIME BETWEEN CAST(:time AS TIMESTAMP) - INTERVAL '1 minute'
                AND CAST(:time AS TIMESTAMP) + INTERVAL '1 minute' AND user_id != :userId""", nativeQuery = true)
    List<Geolocation> findInSquare(Double myLat, Double myLng, Double aroundLat, Double aroundLng, Long userId, String time);
}

