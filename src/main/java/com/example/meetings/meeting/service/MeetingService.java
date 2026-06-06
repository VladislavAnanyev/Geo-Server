package com.example.meetings.meeting.service;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.SubscriptionRequiredException;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
import com.example.meetings.subscription.service.SubscriptionService;
import com.example.meetings.user.model.domain.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

@Service
@Slf4j
@AllArgsConstructor
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private SubscriptionService subscriptionService;

    public List<MeetingView> getMyMeetings(Long userId, LocalDate startAt, LocalDate endAt) {
        startAt = isNull(startAt) ? LocalDate.now() : startAt;
        endAt = isNull(endAt) ? LocalDate.now() : endAt;

        return meetingRepository.getMyMeetings(userId, startAt.atStartOfDay(), endAt.plusDays(1).atStartOfDay());
    }

    /**
     * Для бесплатной подписки доступны встречи только за сегодня и вчера.
     */
    public void validateMeetingHistoryAccess(Long userId, LocalDate startAt, LocalDate endAt) {
        startAt = isNull(startAt) ? LocalDate.now() : startAt;
        endAt = isNull(endAt) ? LocalDate.now() : endAt;

        if (subscriptionService.isPremium(userId)) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate freeStart = today.minusDays(1);

        if (startAt.isBefore(freeStart) || endAt.isAfter(today)) {
            throw new SubscriptionRequiredException(
                    "exception.subscription.meetings.history.limit",
                    GlobalErrorCode.ERROR_SUBSCRIPTION_MEETINGS_HISTORY_LIMIT
            );
        }
    }

    public List<Meeting> findNowMeetings(Geolocation geolocation) {
        LocalDateTime time = geolocation.getUpdatedAt();
        User geolocationSender = geolocation.getUser();

        List<Geolocation> peopleNearMe = geolocationService.findInSquare(geolocation, 50, time)
                .stream()
                .filter(geo -> !geo.getUser().getUserId().equals(geolocationSender.getUserId()))
                .toList();

        List<Meeting> newMeetings = new ArrayList<>();
        if (peopleNearMe.isEmpty()) {
            return newMeetings;
        }

        for (Geolocation peopleGeolocation : peopleNearMe) {
            List<Meeting> meetings = meetingRepository.getMeetings(
                    geolocationSender.getUserId(),
                    peopleGeolocation.getUser().getUserId(),
                    time.toLocalDate().atStartOfDay(), time.toLocalDate().plusDays(1).atStartOfDay()
            );

            if (meetings.isEmpty()) {
                if (!geolocationSender.getUserId().equals(peopleGeolocation.getUser().getUserId())) {
                    Meeting meeting = new Meeting().setFirstUser(geolocationSender)
                            .setSecondUser(peopleGeolocation.getUser())
                            .setLat(geolocation.getLat())
                            .setLng(geolocation.getLng())
                            .setTime(geolocation.getUpdatedAt());

                    meetingRepository.save(meeting);
                    newMeetings.add(meeting);
                }
            }
        }

        return newMeetings;
    }

    public Meeting findMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId).orElseThrow();
    }
}
