package com.example.meetings.meeting.repository;

import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = """
            SELECT M.MEETING_ID AS meetingId, lat, lng, time, f.USER_ID AS firstUserId, S.USER_ID AS secondUserId
            FROM MEETINGS M
                     LEFT OUTER JOIN USERS F ON M.FIRST_USER_ID = F.USER_ID
                     LEFT OUTER JOIN USERS S ON M.SECOND_USER_ID = S.USER_ID
            WHERE (FIRST_USER_ID = :userId OR SECOND_USER_ID = :userId)
              AND TIME >= :startAt AND TIME <= :endAt""", nativeQuery = true)
    List<MeetingView> getMyMeetings(Long userId, LocalDateTime startAt, LocalDateTime endAt);

    @Query(value = """
            INSERT INTO meetings (meeting_id, lat, lng, time, first_user_id, second_user_id, bucket)
            VALUES (nextval('hibernate_sequence'), :lat, :lng, :time, :firstUserId, :secondUserId, :bucket)
            ON CONFLICT (first_user_id, second_user_id, bucket) DO NOTHING
            RETURNING meeting_id
            """, nativeQuery = true)
    Optional<Long> insertIfAbsent(@Param("lat") Double lat,
                                  @Param("lng") Double lng,
                                  @Param("time") LocalDateTime time,
                                  @Param("firstUserId") Long firstUserId,
                                  @Param("secondUserId") Long secondUserId,
                                  @Param("bucket") String bucket);

}
