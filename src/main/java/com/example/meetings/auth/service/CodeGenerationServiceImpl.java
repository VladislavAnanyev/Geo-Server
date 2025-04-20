package com.example.meetings.auth.service;

import com.example.meetings.common.utils.CodeUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "codesender.enabled", havingValue = "true")
public class CodeGenerationServiceImpl implements CodeGenerationService {
    @Override
    public String generate() {
        return CodeUtil.generateShortCode();
    }
}
