package com.example.meetings.geolocation.facade;

import com.example.meetings.common.rabbit.eventtype.MeetingType;
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

import static com.example.meetings.util.Constants.GEOLOCATION_MEETING_EXECUTOR;
import static com.example.meetings.util.Constants.NOTIFICATION_MEETING_DESCRIPTION;
import static com.example.meetings.util.Constants.NOTIFICATION_MEETING_TITLE;

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

    /**
     * Ищет текущие встречи по координатам и отправляет события и push-уведомления участникам.
     *
     * @param userId    идентификатор пользователя, обновившего геолокацию
     * @param lat       широта
     * @param lng       долгота
     * @param updatedAt время обновления геолокации
     */
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
            notificationService.send(NOTIFICATION_MEETING_TITLE, NOTIFICATION_MEETING_DESCRIPTION, meeting);
        });
    }
}
