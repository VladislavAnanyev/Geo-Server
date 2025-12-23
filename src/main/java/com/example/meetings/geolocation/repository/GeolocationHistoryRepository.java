package com.example.meetings.geolocation.repository;

import com.example.meetings.geolocation.model.GeolocationHistory;
import org.springframework.data.repository.CrudRepository;

public interface GeolocationHistoryRepository extends CrudRepository<GeolocationHistory, Long> {
}
