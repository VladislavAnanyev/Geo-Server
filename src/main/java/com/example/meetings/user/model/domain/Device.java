package com.example.meetings.user.model.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Accessors(chain = true)
public class Device {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long deviceId;

    @Column(name = "fcm_token")
    private String fcmToken;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
