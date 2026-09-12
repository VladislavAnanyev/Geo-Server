package com.example.meetings.location.model.dto;

import lombok.Data;

@Data
public class LocationRequest {
    private String name;
    private Double lat;
    private Double lng;
    private Integer radius;
    private String image;
}
