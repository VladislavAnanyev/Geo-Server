package com.example.meetings.meeting.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class GeolocationModel {
    private Double lat;
    private Double lng;
    private Date time;
}
