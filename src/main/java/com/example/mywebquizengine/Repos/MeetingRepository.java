package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Geo.Meeting;
import com.example.mywebquizengine.Model.Projection.Api.MeetingForApiViewCustomQuery;
import com.example.mywebquizengine.Model.Projection.MeetingView;
import com.example.mywebquizengine.Model.Projection.MeetingViewCustomQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = "SELECT *\n" +
            "FROM MEETINGS\n" +
            "WHERE (FIRST_USER_USERNAME = :firstUsername \n" +
            "  AND SECOND_USER_USERNAME = :secondUsername or FIRST_USER_USERNAME = :secondUsername \n" +
            "    AND SECOND_USER_USERNAME = :firstUsername) and TIME BETWEEN timestampadd(DAY, 0,CURDATE()) AND timestampadd(DAY, 1,CURDATE())", nativeQuery = true)
    List<Meeting> getMeetings(String firstUsername, String secondUsername);

    @Query(value = "SELECT id, lat, lng, time, f.username as first_username, F.avatar as first_avatar, " +
            "F.first_name as first_firstName, F.last_name as first_lastName, S.USERNAME as second_username, " +
            "S.AVATAR as second_avatar, S.FIRST_NAME as second_firstName, S.LAST_NAME as second_lastName\n" +
            "FROM MEETINGS LEFT OUTER JOIN USERS F on MEETINGS.FIRST_USER_USERNAME = F.USERNAME\n" +
            "LEFT OUTER JOIN USERS S ON MEETINGS.SECOND_USER_USERNAME = S.USERNAME\n" +
            "WHERE (FIRST_USER_USERNAME = :username \n" +
            "  AND SECOND_USER_USERNAME != :username or FIRST_USER_USERNAME != :username \n" +
            "    AND SECOND_USER_USERNAME = :username) and TIME BETWEEN :dateStart AND :dateEnd", nativeQuery = true)
    List<MeetingForApiViewCustomQuery> getMyMeetingsTodayApi(String username, String dateStart, String dateEnd);

    @Query(value = "SELECT id, lat, lng, time, f.username as first_username, F.avatar as first_avatar, " +
            "F.first_name as first_firstName, F.last_name as first_lastName, S.USERNAME as second_username, " +
            "S.AVATAR as second_avatar, S.FIRST_NAME as second_firstName, S.LAST_NAME as second_lastName\n" +
            "FROM MEETINGS LEFT OUTER JOIN USERS F on MEETINGS.FIRST_USER_USERNAME = F.USERNAME\n" +
            "LEFT OUTER JOIN USERS S ON MEETINGS.SECOND_USER_USERNAME = S.USERNAME\n" +
            "WHERE (FIRST_USER_USERNAME = :username \n" +
            "  AND SECOND_USER_USERNAME != :username or FIRST_USER_USERNAME != :username \n" +
            "    AND SECOND_USER_USERNAME = :username) and TIME BETWEEN :dateStart AND :dateEnd", nativeQuery = true)
    List<MeetingViewCustomQuery> getMyMeetingsToday(String username, String dateStart, String dateEnd);

    MeetingView findMeetingById(Long id);

    MeetingForApiViewCustomQuery getMeetingById(Long id);

}
