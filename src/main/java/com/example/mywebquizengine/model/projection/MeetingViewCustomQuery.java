package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingViewCustomQuery {
    Long getId();

    @Value("#{@geoService.getUserForMeeting(target.first_username, target.second_username)}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();

    Date getTime();
}