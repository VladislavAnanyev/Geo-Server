package com.example.meetings.geolocation.facade;

import com.example.meetings.common.rabbit.eventtype.MeetingType;
import static com.example.meetings.config.GeolocationMeetingAsyncConfig.GEOLOCATION_MEETING_EXECUTOR;
import com.example.meetings.common.service.EventService;
import com.example.meetings.common.service.NotificationService;
import com.example.meetings.common.utils.ProjectionUtil;
import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingViewForNotification;
import com.example.meetings.meeting.service.MeetingService;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static com.example.meetings.common.utils.Const.MEETING;
import static com.example.meetings.common.utils.Const.MEETING_DESCRIPTION;

/**
 * Асинхронный запуск поиска встреч после сохранения геолокации.
 * В async-поток передаются только простые значения, а сущности поднимаются в новой транзакции.
 */
@Service
@RequiredArgsConstructor
public class GeolocationMeetingAsyncService {

    private final MeetingService meetingService;
    private final UserService userService;
    private final EventService eventService;
    private final NotificationService notificationService;
    private final ProjectionUtil projectionUtil;

    @Async(GEOLOCATION_MEETING_EXECUTOR)
    @Transactional
    public void searchMeetings(Long userId, Double lat, Double lng, LocalDateTime updatedAt) {
        Geolocation geolocation = new Geolocation()
                .setUser(userService.loadUserByUserId(userId))
                .setLat(lat)
                .setLng(lng)
                .setUpdatedAt(updatedAt);

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
}
