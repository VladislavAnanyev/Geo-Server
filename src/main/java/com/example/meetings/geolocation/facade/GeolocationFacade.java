package com.example.meetings.geolocation.facade;

import com.example.meetings.meeting.model.GeolocationDto;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;

import java.util.List;
import java.util.Optional;

public interface GeolocationFacade {

    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    GetGeolocationsResult getFriendsGeolocations(Long userId);

    Optional<GeolocationDto> getRecentOwnGeolocation(Long userId);

    List<GeolocationDto> getPeopleInSquare(Long userId);
}
