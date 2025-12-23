package com.example.meetings.meeting.repository;

import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = """
            SELECT *
            FROM MEETINGS
            WHERE (FIRST_USER_ID = :firstUserId
              AND SECOND_USER_ID = :secondUserId OR FIRST_USER_ID = :secondUserId
                AND SECOND_USER_ID = :firstUserId)
                AND TIME BETWEEN :startAt AND :endAt
                """, nativeQuery = true)
    List<Meeting> getMeetings(Long firstUserId, Long secondUserId, LocalDateTime startAt, LocalDateTime endAt);

    @Query(value = """
            SELECT M.MEETING_ID AS meetingId, lat, lng, time, f.USER_ID AS firstUserId, S.USER_ID AS secondUserId
            FROM MEETINGS M
                     LEFT OUTER JOIN USERS F ON M.FIRST_USER_ID = F.USER_ID
                     LEFT OUTER JOIN USERS S ON M.SECOND_USER_ID = S.USER_ID
                     LEFT OUTER JOIN REQUESTS R ON M.MEETING_ID = R.MEETING_ID AND sender_user_id = :userId
            WHERE (FIRST_USER_ID = :userId OR SECOND_USER_ID = :userId) AND (R.status IS NULL OR NOT (R.status = 'REJECTED' AND R.sender_user_id = :userId))
            AND TIME >=:startAt AND TIME <=:endAt""", nativeQuery = true)
    List<MeetingView> getMyMeetings(Long userId, LocalDateTime startAt, LocalDateTime endAt);

}
