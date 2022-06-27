package com.example.mywebquizengine.meeting.service;

import com.example.mywebquizengine.common.NotificationService;
import com.example.mywebquizengine.common.rabbit.MeetingType;
import com.example.mywebquizengine.common.utils.ProjectionUtil;
import com.example.mywebquizengine.meeting.GeolocationModel;
import com.example.mywebquizengine.meeting.model.domain.Geolocation;
import com.example.mywebquizengine.meeting.model.domain.Meeting;
import com.example.mywebquizengine.meeting.model.dto.output.GeolocationView;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingViewForNotification;
import com.example.mywebquizengine.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@Service
public class GeolocationFacadeImpl implements GeolocationFacade {

    @Autowired
    private GeoService geoService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectionUtil projectionUtil;

    @Override
    public void processGeolocation(Long userId, GeolocationModel geolocationModel) {
        Geolocation geolocation = geoService.saveGeolocation(userId, geolocationModel);
        List<Meeting> meetings = geoService.findMeetings(geolocation);
        for (Meeting meeting : meetings) {
            MeetingViewForNotification meetingView = projectionUtil.parse(
                    meeting,
                    MeetingViewForNotification.class
            );
            Set<User> users = Set.of(meeting.getFirstUser(), meeting.getSecondUser());
            notificationService.send(meetingView, users, MeetingType.MEETING);
        }
    }

    @Override
    public List<MeetingView> getMeetings(Long userId, String date) {
        return geoService.getMyMeetings(userId, date);
    }

    @Override
    public void addGeolocationHistory(MultipartFile multipartFile, Long userId) {
        geoService.loadGeolocationHistory(multipartFile, userId);
    }

    @Override
    public List<GeolocationView> getAllUsersGeoNow(Long userId) {
        return geoService.getAllUsersGeo(userId);
    }

    @Override
    public List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time) {
        return geoService.findInSquare(userId, geolocation, size, time);
    }

}
