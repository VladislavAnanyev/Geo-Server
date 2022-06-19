package com.example.mywebquizengine.model.geo.dto.output;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface GeolocationView {
    Double getLat();
    Double getLng();
    Date getTime();
    @Value("#{target.username}")
    String getUser();
}
