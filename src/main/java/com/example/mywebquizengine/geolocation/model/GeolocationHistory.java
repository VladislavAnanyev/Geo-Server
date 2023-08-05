package com.example.mywebquizengine.geolocation.model;

import com.example.mywebquizengine.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class GeolocationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long geolocationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private Double lat;

    private Double lng;

    private LocalDateTime createdAt;
}
