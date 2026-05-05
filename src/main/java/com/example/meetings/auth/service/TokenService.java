package com.example.meetings.auth.service;

public interface TokenService {
    String createToken(Long userId);
}
