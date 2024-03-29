package com.example.meetings.auth.model;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "users", timeToLive = 18000000)
@Accessors(chain = true)
public class UserToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String fingerprint;
}

