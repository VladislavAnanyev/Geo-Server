package com.example.meetings.meeting.facade;

import com.example.meetings.common.rabbit.eventtype.MeetingType;
import com.example.meetings.common.service.EventService;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.meetings.common.utils.Const.MEETING;
import static com.example.meetings.common.utils.Const.MEETING_DESCRIPTION;
import static java.util.concurrent.Executors.newSingleThreadExecutor;

@Service
public class GeolocationFacadeImpl implements GeolocationFacade {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private EventService eventService;

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
                    meetings.forEach(meeting -> {
                        eventService.send(
                                projectionUtil.parse(meeting, MeetingViewForNotification.class),
                                Set.of(meeting.getFirstUser(), meeting.getSecondUser()),
                                MeetingType.MEETING
                        );
                        notificationService.send(MEETING, MEETING_DESCRIPTION, meeting);
                    });
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

        return new GetGeolocationsResult()
                .setItems(
                        geolocations.stream()
                                .map(geolocation -> new GeolocationDto()
                                        .setUserId(geolocation.getUser().getUserId())
                                        .setLat(geolocation.getLat())
                                        .setLng(geolocation.getLng())
                                        .setFirstName(geolocation.getUser().getFirstName())
                                        .setLastName(geolocation.getUser().getLastName()))
                                .collect(Collectors.toList())
                );
    }

    @Override
    public List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, String time) {
        return geolocationService.findInSquare(userId, geolocation, size, time);
    }
}
