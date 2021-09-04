package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Geolocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeolocationRepository extends CrudRepository<Geolocation, String> {

    @Query(value = "SELECT * FROM GEOLOCATION WHERE USERNAME != :username", nativeQuery = true)
    List<Geolocation> getAll(String username);

    @Query(value = "select * from GEOLOCATION where LAT between :myLat - :aroundLat and :myLat + :aroundLat and LNG between :myLng - :aroundLng and :myLng + :aroundLng and USERNAME != :username", nativeQuery = true)
    List<Geolocation> findInSquare(Double myLat, Double myLng, Double aroundLat, Double aroundLng, String username);


    }

