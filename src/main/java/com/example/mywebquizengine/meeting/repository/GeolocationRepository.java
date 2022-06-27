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
            select * from (SELECT * FROM GEOLOCATIONS WHERE TIME IN (SELECT MAX(TIME)
                                               FROM GEOLOCATIONS
                                               WHERE TIME <= timestampadd(MINUTE, 1, :time)
                                                GROUP BY USER_ID)) where LAT between :myLat - :aroundLat and :myLat + :aroundLat 
                                                and LNG between :myLng - :aroundLng and :myLng + :aroundLng 
                                                and TIME BETWEEN timestampadd(MINUTE, -1, :time) 
                                                AND timestampadd(MINUTE, 1, :time) and user_id != :userId""", nativeQuery = true)
    List<Geolocation> findInSquare(Double myLat, Double myLng, Double aroundLat, Double aroundLng, Long userId, String time);


}

