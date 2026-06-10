package com.example.meetings.meeting.facade;

import com.example.meetings.meeting.model.GetMeetingTimesResult;
import com.example.meetings.meeting.model.GetMeetingsResult;

import java.time.LocalDate;

/**
 * Фасад сценариев API истории встреч.
 */
public interface MeetingsFacade {

    /**
     * Возвращает встречи пользователя за указанный период.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата периода (включительно)
     * @param endAt   конечная дата периода (включительно)
     * @return результат со списком встреч пользователя
     */
    GetMeetingsResult getMeetings(Long userId, LocalDate startAt, LocalDate endAt);

    /**
     * Возвращает времена встреч пользователя за указанный период.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата периода (включительно)
     * @param endAt   конечная дата периода (включительно)
     * @return результат со списком времён встреч
     */
    GetMeetingTimesResult getMeetingTimes(Long userId, LocalDate startAt, LocalDate endAt);

}
