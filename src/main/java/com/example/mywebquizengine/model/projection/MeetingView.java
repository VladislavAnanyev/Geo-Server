package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface MeetingView {

    Long getId();

    @Value("#{target.secondUser}")
    UserCommonView getUser();

    Double getLng();
    Double getLat();

    Date getTime();

}
