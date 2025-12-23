package com.example.meetings.geolocation.facade;

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
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;

import static com.example.meetings.common.utils.Const.MEETING;
import static com.example.meetings.common.utils.Const.MEETING_DESCRIPTION;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.stream.Collectors.toList;

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
    @Transactional
    public void addGeolocation(Long userId, GeolocationModel geolocationModel) {
        Geolocation geolocation = geolocationService.saveGeolocation(userId, geolocationModel);
        searchMeetings(geolocation);
    }


//    @Async("threadPoolTaskExecutor") // todo проверить
    public void searchMeetings(Geolocation geolocation) {
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
                                        .setFirstName(geolocation.getUser().getFirstName()))
                                .collect(toList())
                );
    }

    @Override
    public List<Geolocation> getPeopleInSquare(Long userId, Geolocation geolocation, Integer size, LocalDateTime time) {
        return geolocationService.findInSquare(userId, geolocation, size, time);
    }
}
