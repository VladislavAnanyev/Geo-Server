package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingViewCustomQuery {
    Long getId();

    @Value("#{new com.example.mywebquizengine.model.User(target.first_username, target.first_firstName, target.first_lastName, T(com.example.mywebquizengine.model.Photo).getList(target.first_avatar))}")
    UserCommonView getFirstUser();

    @Value("#{new com.example.mywebquizengine.model.User(target.second_username, target.second_firstName, target.second_lastName, T(com.example.mywebquizengine.model.Photo).getList(target.second_avatar))}")
    UserCommonView getSecondUser();

    Double getLng();
    Double getLat();

    Date getTime();
}
