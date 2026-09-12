package com.example.meetings.auth.service;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "codesender.enabled", havingValue = "false")
public class MockCodeGenerationService implements CodeGenerationService {
    @Override
    public String generate() {
        return "1234";
    }
}
