package com.example.mywebquizengine.meeting.repository;

import com.example.mywebquizengine.meeting.model.domain.Meeting;
import com.example.mywebquizengine.meeting.model.dto.output.MeetingView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = """
            SELECT *
            FROM MEETINGS
            WHERE (FIRST_USER_ID = :firstUserId\s
              AND SECOND_USER_ID = :secondUserId OR FIRST_USER_ID = :secondUserId\s
                AND SECOND_USER_ID = :firstUserId) AND TIME BETWEEN CAST(:date AS TIMESTAMP) + INTERVAL '0 days'
                 AND CAST(:date AS TIMESTAMP) + INTERVAL '1 day'""", nativeQuery = true)
    List<Meeting> getMeetings(Long firstUserId, Long secondUserId, String date);

    @Query(value = """
            SELECT MEETINGS.MEETING_ID AS meetingId, lat, lng, time, f.USER_ID AS firstUserId, S.USER_ID AS secondUserId
            FROM MEETINGS
                        LEFT OUTER JOIN USERS F ON MEETINGS.FIRST_USER_ID = F.USER_ID
                        LEFT OUTER JOIN USERS S ON MEETINGS.SECOND_USER_ID = S.USER_ID
            WHERE (FIRST_USER_ID = :userId
                          AND SECOND_USER_ID != :userId OR FIRST_USER_ID != :userId
                          AND SECOND_USER_ID = :userId)
            AND CAST(TIME AS VARCHAR) LIKE CONCAT(:date, '%')""", nativeQuery = true)
    List<MeetingView> getMyMeetings(Long userId, String date);

}
