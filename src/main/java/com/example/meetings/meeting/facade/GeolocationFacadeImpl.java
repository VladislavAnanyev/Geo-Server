package com.example.meetings.meeting.facade;

import com.example.meetings.common.rabbit.eventtype.MeetingType;
import com.example.meetings.common.service.NotificationService;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.meeting.model.*;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingViewForNotification;
import com.example.meetings.meeting.service.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Service
public class GeolocationFacadeImpl implements GeolocationFacade {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProjectionUtil projectionUtil;

    @Override
    public void addGeolocation(Long userId, GeolocationModel geolocationModel) {
        Geolocation geolocation = geolocationService.saveGeolocation(userId, geolocationModel);
        newSingleThreadExecutor().execute(
                () -> {
                    List<Meeting> meetings = meetingService.findNowMeetings(geolocation);
                    meetings.forEach(meeting -> notificationService.send(
                            projectionUtil.parse(meeting, MeetingViewForNotification.class),
                            Set.of(meeting.getFirstUser(), meeting.getSecondUser()),
                            MeetingType.MEETING
                    ));
                }
        );
    }

    @Override
    public GetMeetingsResult getMeetings(Long userId, String date) {
        return new GetMeetingsResult()
                .setMeetings(meetingService.getMyMeetings(userId, date));
    }

    @Override
    public GetGeolocationsResult getFriendsGeolocations(Long userId) {
        List<Geolocation> geolocations = geolocationService.getFriendsGeolocations(userId);
        List<GeolocationDto> geolocationDtoList = new ArrayList<>();
        for (Geolocation geolocation : geolocations) {
            geolocationDtoList.add(
                    new GeolocationDto()
                            .setUserId(geolocation.getUser().getUserId())
                            .setLat(geolocation.getLat())
                            .setLng(geolocation.getLng())
                            .setFirstName(geolocation.getUser().getFirstName())
                            .setLastName(geolocation.getUser().getLastName())
            );
        }

        return new GetGeolocationsResult()
                .setItems(geolocationDtoList);
    }

    @Override
    public List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time) {
        return geolocationService.findInSquare(userId, geolocation, size, time);
    }

}
