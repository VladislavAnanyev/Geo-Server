package com.example.meetings.geolocation.facade;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.meeting.model.GeolocationDto;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GeolocationFacade {

    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    GetGeolocationsResult getFriendsGeolocations(Long userId);

    Optional<GeolocationDto> getRecentOwnGeolocation(Long userId);

    List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, LocalDateTime time);
}
