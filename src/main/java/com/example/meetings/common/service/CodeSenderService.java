package com.example.meetings.common.service;

import org.springframework.stereotype.Service;

@Service
public interface CodeSenderService {
    void sendCodeToPhone(String code, String phone);
}
