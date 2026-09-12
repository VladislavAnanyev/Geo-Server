package com.example.meetings.meeting.facade;

import com.example.meetings.meeting.model.GetMeetingTimesResult;
import com.example.meetings.meeting.model.GetMeetingsResult;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.service.MeetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Реализация фасада истории встреч.
 */
@Service
@RequiredArgsConstructor
public class MeetingsFacadeImpl implements MeetingsFacade {

    private final MeetingService meetingService;

    /**
     * Возвращает встречи пользователя за указанный период.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата периода (включительно)
     * @param endAt   конечная дата периода (включительно)
     * @return результат со списком встреч пользователя
     */
    @Override
    public GetMeetingsResult getMeetings(Long userId, LocalDate startAt, LocalDate endAt) {
        meetingService.validateMeetingHistoryAccess(userId, startAt, endAt);

        return new GetMeetingsResult()
                .setMeetings(meetingService.getMyMeetings(userId, startAt, endAt));
    }

    /**
     * Возвращает времена встреч пользователя за указанный период.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата периода (включительно)
     * @param endAt   конечная дата периода (включительно)
     * @return результат со списком времён встреч
     */
    @Override
    public GetMeetingTimesResult getMeetingTimes(Long userId, LocalDate startAt, LocalDate endAt) {
        return new GetMeetingTimesResult()
                .setTimes(meetingService.getMyMeetings(userId, startAt, endAt).stream()
                        .map(MeetingView::getTime)
                        .toList());
    }

}
