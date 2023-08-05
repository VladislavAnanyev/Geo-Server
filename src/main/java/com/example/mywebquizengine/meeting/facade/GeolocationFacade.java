package com.example.mywebquizengine.meeting.facade;

import com.example.mywebquizengine.meeting.model.GetGeolocationsResult;
import com.example.mywebquizengine.meeting.model.GeolocationModel;
import com.example.mywebquizengine.meeting.model.GetMeetingsResult;
import com.example.mywebquizengine.geolocation.model.Geolocation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface GeolocationFacade {

    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    GetMeetingsResult getMeetings(Long userId, String date);

    GetGeolocationsResult getFriendsGeolocations(Long userId);

    List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time);
}
