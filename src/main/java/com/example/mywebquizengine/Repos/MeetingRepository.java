package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Meeting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MeetingRepository extends CrudRepository<Meeting, Long> {

    @Query(value = "SELECT *\n" +
            "FROM MEETING\n" +
            "WHERE (FIRST_USER_USERNAME = :firstUsername \n" +
            "  AND SECOND_USER_USERNAME = :secondUsername or FIRST_USER_USERNAME = :secondUsername \n" +
            "    AND SECOND_USER_USERNAME = :firstUsername) and TIME BETWEEN :dateStart AND :dateEnd", nativeQuery = true)
    List<Meeting> getMeetings(String firstUsername, String secondUsername, String dateStart, String dateEnd);

    @Query(value = "SELECT *\n" +
            "FROM MEETING\n" +
            "WHERE (FIRST_USER_USERNAME = :username \n" +
            "  AND SECOND_USER_USERNAME != :username or FIRST_USER_USERNAME != :username \n" +
            "    AND SECOND_USER_USERNAME = :username) and TIME BETWEEN :dateStart AND :dateEnd", nativeQuery = true)
    List<Meeting> getMyMeetingsToday(String username, String dateStart, String dateEnd);

}
