package com.example.meetings.meeting.service;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
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

    public List<MeetingView> getMyMeetings(Long userId, LocalDate startAt, LocalDate endAt) {
        startAt = isNull(startAt) ? LocalDate.now() : startAt;
        endAt = isNull(endAt) ? LocalDate.now() : endAt;

        return meetingRepository.getMyMeetings(userId, startAt.atStartOfDay(), endAt.plusDays(1).atStartOfDay());
    }

    public List<Meeting> findNowMeetings(Geolocation geolocation) {
        LocalDateTime time = geolocation.getUpdatedAt();
        User geolocationSender = geolocation.getUser();

        List<Geolocation> peopleNearMe = geolocationService.findInSquare(
                geolocationSender.getUserId(), geolocation,
                20, time
        );

        List<Meeting> newMeetings = new ArrayList<>();
        if (peopleNearMe.size() == 0) {
            return newMeetings;
        }

        for (Geolocation peopleGeolocation : peopleNearMe) {
            List<Meeting> meetings = meetingRepository.getMeetings(
                    geolocationSender.getUserId(),
                    peopleGeolocation.getUser().getUserId(),
                    time.toLocalDate().atStartOfDay(), time.toLocalDate().plusDays(1).atStartOfDay()
            );

            if (meetings.size() == 0) {
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

}
