package com.example.mywebquizengine.geolocation.repository;

import com.example.mywebquizengine.geolocation.model.GeolocationHistory;
import org.springframework.data.repository.CrudRepository;

public interface GeolocationHistoryRepository extends CrudRepository<GeolocationHistory, Long> {
}
