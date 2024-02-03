package com.example.meetings.user.model.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long deviceId;

    private String deviceToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
