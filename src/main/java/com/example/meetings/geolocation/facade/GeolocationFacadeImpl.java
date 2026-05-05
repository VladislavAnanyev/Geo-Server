package com.example.meetings.geolocation.facade;

import com.example.meetings.common.rabbit.eventtype.MeetingType;
import com.example.meetings.common.service.EventService;
import com.example.meetings.common.service.NotificationService;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.meeting.model.GeolocationDto;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingViewForNotification;
import com.example.meetings.meeting.service.MeetingService;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.example.meetings.common.utils.Const.MEETING;
import static com.example.meetings.common.utils.Const.MEETING_DESCRIPTION;
import static java.util.stream.Collectors.toList;

@Service
public class GeolocationFacadeImpl implements GeolocationFacade {

    @Value("${geolocation.recent-window-minutes:30}")
    private int recentWindowMinutes;

    @Value("${geolocation.people-nearby-radius-meters:1000}")
    private int peopleNearbyRadiusMeters;

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
                                .map(this::toGeolocationDto)
                                .collect(toList())
                );
    }

    @Override
    public Optional<GeolocationDto> getRecentOwnGeolocation(Long userId) {
        return geolocationService.getRecentGeolocation(userId, recentWindowMinutes).map(this::toGeolocationDto);
    }

    @Override
    public List<GeolocationDto> getPeopleInSquare(Long userId) {
        Optional<GeolocationDto> recent = geolocationService.getRecentGeolocation(userId, recentWindowMinutes).map(this::toGeolocationDto);
        if (recent.isEmpty()) {
            return List.of();
        }
        GeolocationDto centerDto = recent.get();
        if (centerDto.getLat() == null || centerDto.getLng() == null) {
            return List.of();
        }
        Geolocation center = new Geolocation().setLat(centerDto.getLat()).setLng(centerDto.getLng());
        return geolocationService.findInSquare(center, peopleNearbyRadiusMeters, LocalDateTime.now())
                .stream()
                .filter(g -> g.getUser() != null && !Objects.equals(g.getUser().getUserId(), userId))
                .map(this::toGeolocationDto)
                .collect(toList());
    }

    private GeolocationDto toGeolocationDto(Geolocation geolocation) {
        User user = geolocation.getUser();
        return new GeolocationDto()
                .setLat(geolocation.getLat())
                .setLng(geolocation.getLng())
                .setLastUpdateAt(geolocation.getUpdatedAt())
                .setUserId(user.getUserId())
                .setFirstName(user.getFirstName())
                .setUsername(user.getUsername())
                .setAvatar(user.getMainPhoto().getUrl());
    }
}
