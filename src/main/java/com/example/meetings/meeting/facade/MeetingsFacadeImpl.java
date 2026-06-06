package com.example.meetings.meeting.facade;

import com.example.meetings.meeting.model.GetMeetingTimesResult;
import com.example.meetings.meeting.model.GetMeetingsResult;
import com.example.meetings.meeting.model.dto.output.MeetingView;
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
        meetingService.validateMeetingHistoryAccess(userId, startAt, endAt);

        return new GetMeetingsResult()
                .setMeetings(meetingService.getMyMeetings(userId, startAt, endAt));
    }

    @Override
    public GetMeetingTimesResult getMeetingTimes(Long userId, LocalDate startAt, LocalDate endAt) {
        return new GetMeetingTimesResult()
                .setTimes(meetingService.getMyMeetings(userId, startAt, endAt).stream()
                        .map(MeetingView::getTime)
                        .toList());
    }

}
