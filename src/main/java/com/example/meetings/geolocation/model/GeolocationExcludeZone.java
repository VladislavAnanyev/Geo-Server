package com.example.meetings.geolocation.model;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Getter
@Setter
@Accessors(chain = true)
@Entity(name = "GEOLOCATION_EXCLUDE_ZONES")
public class GeolocationExcludeZone {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "lat", nullable = false)
    private Double lat;

    @Column(name = "lng", nullable = false)
    private Double lng;

    @Column(name = "radius_meters", nullable = false)
    private Integer radiusMeters;

    @ColumnDefault("true")
    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;
}

