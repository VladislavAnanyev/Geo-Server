package com.example.meetings.geolocation.repository;

import com.example.meetings.geolocation.model.GeolocationHistory;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface GeolocationHistoryRepository extends CrudRepository<GeolocationHistory, Long> {

    List<GeolocationHistory> findByUser_UserIdAndCreatedAtAfterOrderByCreatedAtDesc(
            Long userId, LocalDateTime since);
}
