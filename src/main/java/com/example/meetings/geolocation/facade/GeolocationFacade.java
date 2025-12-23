package com.example.meetings.geolocation.facade;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;

import java.time.LocalDateTime;
import java.util.List;

public interface GeolocationFacade {

    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    GetGeolocationsResult getFriendsGeolocations(Long userId);

    List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, LocalDateTime time);
}
