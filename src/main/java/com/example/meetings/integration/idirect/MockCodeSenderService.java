package com.example.meetings.integration.idirect;

import com.example.meetings.common.service.CodeSenderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@ConditionalOnProperty(value = "codesender.enabled", havingValue = "false")
public class MockCodeSenderService implements CodeSenderService {
    @Override
    public void sendCodeToPhone(String code, String phone) {
        log.info("Отправка кода на номер телефона отключена");
    }
}
