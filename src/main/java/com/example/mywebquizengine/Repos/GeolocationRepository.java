package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Geolocation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GeolocationRepository extends CrudRepository<Geolocation, String> {

    @Query(value = "SELECT * FROM GEOLOCATION", nativeQuery = true)
    List<Geolocation> getAll();
}

