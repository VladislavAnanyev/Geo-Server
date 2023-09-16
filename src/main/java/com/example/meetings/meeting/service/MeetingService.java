package com.example.meetings.meeting.service;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private GeolocationService geolocationService;

    public List<MeetingView> getMyMeetings(Long userId, String date) {
        if (date == null) {
            Calendar calendar = new GregorianCalendar();
            Timestamp timestamp = Timestamp.from(calendar.toInstant());
            date = timestamp.toString().substring(0, 10);
        }

        return meetingRepository.getMyMeetings(userId, date);
    }

    public List<Meeting> findMeetings(Geolocation geolocation) {
        String time = geolocation.getUpdatedAt().toString();

        List<Geolocation> peopleNearMe = geolocationService.findInSquare(
                geolocation.getUser().getUserId(), geolocation,
                20, time
        );

        List<Meeting> newMeetings = new ArrayList<>();
        if (peopleNearMe.size() == 0) {
            return newMeetings;
        }

        for (Geolocation peopleGeolocation : peopleNearMe) {
            List<Meeting> meetings = meetingRepository.getMeetings(geolocation.getUser().getUserId(),
                    peopleGeolocation.getUser().getUserId(),
                    time.substring(0, 10)
            );

            if (meetings.size() == 0) {
                if (!geolocation.getUser().getUserId().equals(peopleGeolocation.getUser().getUserId())) {
                    Meeting meeting = new Meeting();
                    meeting.setFirstUser(geolocation.getUser());
                    meeting.setSecondUser(peopleGeolocation.getUser());
                    meeting.setLat(geolocation.getLat());
                    meeting.setLng(geolocation.getLng());
                    meeting.setTime(geolocation.getUpdatedAt());

                    meetingRepository.save(meeting);
                    newMeetings.add(meeting);
                }
            }
        }

        return newMeetings;
    }

}
