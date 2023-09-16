package com.example.meetings.geolocation.model;

import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "GEOLOCATIONS")
@Accessors(chain = true)
public class Geolocation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long geolocationId;

    @OneToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "user_id")
    private User user;

    private Double lat;

    private Double lng;

    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Geolocation{" +
                "id='" + geolocationId + '\'' +
                ", lat='" + lat + '\'' +
                ", lng='" + lng + '\'' +
                '}';
    }
}
