package com.example.meetings.integration.sigmasms;

import com.example.meetings.common.service.CodeSenderService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(value = "codesender.type", havingValue = "sigma")
public class SigmaSmsService implements CodeSenderService {

    private final SigmaSmsClient client;

    @Override
    public void sendCodeToPhone(String code, String phone) {
        client.telegramCode(phone, code);
    }
}
