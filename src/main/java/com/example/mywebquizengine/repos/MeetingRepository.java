package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.geo.domain.Meeting;

import com.example.mywebquizengine.model.geo.dto.output.MeetingViewCustomQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = """
            SELECT *
            FROM MEETINGS
            WHERE (FIRST_USER_ID = :firstUserId\s
              AND SECOND_USER_ID = :secondUserId or FIRST_USER_ID = :secondUserId\s
                AND SECOND_USER_ID = :firstUserId) and TIME BETWEEN timestampadd(DAY, 0,:date)
                 AND timestampadd(DAY, 1,:date)""", nativeQuery = true)
    List<Meeting> getMeetings(Long firstUserId, Long secondUserId, String date);

    @Query(value = """
            SELECT MEETINGS.MEETING_ID as meetingId, lat, lng, time, f.USER_ID as firstUserId, S.USER_ID as secondUserId
                 FROM MEETINGS
                          LEFT OUTER JOIN USERS F on MEETINGS.FIRST_USER_ID = F.USER_ID
                          LEFT OUTER JOIN USERS S ON MEETINGS.SECOND_USER_ID = S.USER_ID
                          WHERE (FIRST_USER_ID = :userId
                            AND SECOND_USER_ID != :userId or FIRST_USER_ID != :userId
                            AND SECOND_USER_ID = :userId)
                   and TIME like concat(:date, '%')""", nativeQuery = true)
    List<MeetingViewCustomQuery> getMyMeetings(Long userId, String date);

}
