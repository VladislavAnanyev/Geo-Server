package com.example.meetings.meeting.facade;

import com.example.meetings.meeting.model.GetMeetingsResult;

import java.time.LocalDate;

public interface MeetingsFacade {

    GetMeetingsResult getMeetings(Long userId, LocalDate startAt, LocalDate endAt);

}
