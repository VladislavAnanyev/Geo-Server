package com.example.mywebquizengine.auth.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Column;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "users", timeToLive = 18000000)
public class UserToken {
    @Id
    @Column(nullable = false)
    private String refreshToken;

    @Column(nullable = false)
    private Long userId;

    @Column
    private String fingerprint;
}

