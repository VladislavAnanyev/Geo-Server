package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.geo.Geolocation;
import com.example.mywebquizengine.model.projection.GeolocationView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface GeolocationRepository extends CrudRepository<Geolocation, String>, JpaRepository<Geolocation, String> {

    @Query(value = """
SELECT *
FROM GEOLOCATIONS
WHERE TIME IN (
    SELECT MAX(TIME)
    FROM GEOLOCATIONS
    WHERE USERNAME != :username GROUP BY USERNAME)
""", nativeQuery = true)
    List<GeolocationView> getAll(String username);

    @Query(value = """
            select * from (SELECT * FROM GEOLOCATIONS WHERE TIME IN (SELECT MAX(TIME)
                                               FROM GEOLOCATIONS
                                               WHERE TIME <= timestampadd(MINUTE, 1, :time)
                                                GROUP BY USERNAME)) where LAT between :myLat - :aroundLat and :myLat + :aroundLat 
                                                and LNG between :myLng - :aroundLng and :myLng + :aroundLng 
                                                and TIME BETWEEN timestampadd(MINUTE, -1, :time) 
                                                AND timestampadd(MINUTE, 1, :time) and USERNAME != :username""", nativeQuery = true)
    List<Geolocation> findInSquare(Double myLat, Double myLng, Double aroundLat, Double aroundLng, String username, String time);




    @Modifying
    @Transactional
    @Query(value = "UPDATE GEOLOCATIONS SET LAT =:lat, LNG =:lng, time = now() WHERE USERNAME =:username", nativeQuery = true)
    void updateGeo(Double lat, Double lng, String username);

    @Transactional
    @Modifying
    @Query(value = "INSERT INTO GEOLOCATIONS VALUES (:username, :lat, :lng, now())", nativeQuery = true)
    void insertGeo(Double lat, Double lng, String username);


}
