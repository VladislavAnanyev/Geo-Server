package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.geo.Meeting;

import com.example.mywebquizengine.model.projection.MeetingView;
import com.example.mywebquizengine.model.projection.MeetingViewCustomQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = """
            SELECT *
            FROM MEETINGS
            WHERE (FIRST_USER_USERNAME = :firstUsername\s
              AND SECOND_USER_USERNAME = :secondUsername or FIRST_USER_USERNAME = :secondUsername\s
                AND SECOND_USER_USERNAME = :firstUsername) and TIME BETWEEN timestampadd(DAY, 0,:date)
                 AND timestampadd(DAY, 1,:date)""", nativeQuery = true)
    List<Meeting> getMeetings(String firstUsername, String secondUsername, String date);



    @Query(value = """
            SELECT MEETINGS.id, lat, lng, time, f.username as first_username, S.USERNAME as second_username
                 FROM MEETINGS
                          LEFT OUTER JOIN USERS F on MEETINGS.FIRST_USER_USERNAME = F.USERNAME
                          LEFT OUTER JOIN USERS S ON MEETINGS.SECOND_USER_USERNAME = S.USERNAME
                          WHERE (FIRST_USER_USERNAME = :username
                            AND SECOND_USER_USERNAME != :username or FIRST_USER_USERNAME != :username
                            AND SECOND_USER_USERNAME = :username)
                   and TIME like concat(:date, '%')""", nativeQuery = true)
    List<MeetingViewCustomQuery> getMyMeetings(String username, String date);

    MeetingView findMeetingById(Long id);

/*    MeetingForApiViewCustomQuery getMeetingById(Long id);*/

}
