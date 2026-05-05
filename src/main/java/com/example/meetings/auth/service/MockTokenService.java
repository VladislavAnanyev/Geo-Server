package com.example.meetings.auth.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "redis.impl", havingValue = "mock")
public class MockTokenService implements TokenService {
    @Override
    public String createToken(Long userId) {
        return "mock-token";
    }
}
