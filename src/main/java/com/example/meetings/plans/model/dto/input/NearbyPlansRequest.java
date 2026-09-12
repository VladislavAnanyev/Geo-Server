package com.example.meetings.plans.model.dto.input;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class NearbyPlansRequest {
    private String type = "ALL";
    private Double lat;
    private Double lon;
    private Integer radiusM = 3000;
    private Integer page = 0;
    private Integer size = 20;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime fromTime;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime toTime;
    private Boolean excludeOwn = true;
}

