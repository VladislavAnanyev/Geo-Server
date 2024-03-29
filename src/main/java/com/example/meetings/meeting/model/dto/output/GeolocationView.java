package com.example.meetings.meeting.model.dto.output;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface GeolocationView {
    Double getLat();

    Double getLng();

    Date getTime();

    @Value("#{target.username}")
    String getUser();
}
