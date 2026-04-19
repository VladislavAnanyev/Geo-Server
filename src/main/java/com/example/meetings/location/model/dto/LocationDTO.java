package com.example.meetings.location.model.dto;

import com.example.meetings.location.model.Location;
import lombok.Data;

@Data
public class LocationDTO {
    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private Integer radius;
    private String image;
    private Integer peopleCount;

    public LocationDTO(Location location) {
        this.id = location.getId();
        this.name = location.getName();
        this.lat = location.getLat();
        this.lng = location.getLng();
        this.radius = location.getRadius();
        this.image = location.getImage();
        this.peopleCount = location.getPeopleCount();
    }
}
