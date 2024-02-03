package com.example.meetings.meeting.facade;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.meeting.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GeolocationFacade {

    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    GetMeetingsResult getMeetings(Long userId, String date);

    GetGeolocationsResult getFriendsGeolocations(Long userId);

    List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time);
}
