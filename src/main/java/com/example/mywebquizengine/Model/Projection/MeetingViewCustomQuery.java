package com.example.mywebquizengine.Model.Projection;

import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Calendar;

public interface MeetingViewCustomQuery {
    Long getId();

    @Value("#{new com.example.mywebquizengine.Model.User(target.first_username, target.first_firstName, target.first_lastName, target.first_avatar)}")
    UserCommonView getFirstUser();

    @Value("#{new com.example.mywebquizengine.Model.User(target.second_username, target.second_firstName, target.second_lastName, target.second_avatar)}")
    UserCommonView getSecondUser();

    Double getLng();
    Double getLat();

    Timestamp getTime();
}
