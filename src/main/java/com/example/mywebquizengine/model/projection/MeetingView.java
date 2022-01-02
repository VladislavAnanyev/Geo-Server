package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingView {

    Long getId();

    @Value("#{T(com.example.mywebquizengine.service.UserUtil).getUserForMeeting(target.firstUser.username, target.secondUser.username)}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();

    Date getTime();

}
