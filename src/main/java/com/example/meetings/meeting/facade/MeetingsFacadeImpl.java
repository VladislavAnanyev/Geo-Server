package com.example.meetings.meeting.facade;

import com.example.meetings.meeting.model.GetMeetingsResult;
import com.example.meetings.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MeetingsFacadeImpl implements MeetingsFacade {

    private final MeetingService meetingService;

    @Override
    public GetMeetingsResult getMeetings(Long userId, LocalDate startAt, LocalDate endAt) {
        return new GetMeetingsResult()
                .setMeetings(meetingService.getMyMeetings(userId, startAt, endAt));
    }

}
