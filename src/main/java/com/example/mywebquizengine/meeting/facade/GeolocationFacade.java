package com.example.mywebquizengine.meeting.facade;

import com.example.mywebquizengine.meeting.GeolocationModel;
import com.example.mywebquizengine.meeting.model.domain.Geolocation;
import com.example.mywebquizengine.meeting.model.dto.output.GeolocationView;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface GeolocationFacade {

    void processGeolocation(Long userId, GeolocationModel geolocationModel);

    List<MeetingView> getMeetings(Long userId, String date);

    void addGeolocationHistory(MultipartFile multipartFile, Long userId);

    List<GeolocationView> getAllUsersGeoNow(Long userId);

    List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time);
}
