package com.example.meetings.integration.idirect;

import com.example.meetings.common.service.CodeSenderService;
import com.example.meetings.integration.idirect.dto.IDirectMessageRequest;
import com.example.meetings.integration.idirect.dto.IDirectMessageRequest.Content;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(value = "codesender.enabled", havingValue = "true")
public class IDirectSenderService implements CodeSenderService {

    public static final String VOICECODE = "VOICECODE";
    private final IDirectClient iDirectClient;

    public IDirectSenderService(IDirectClient iDirectClient) {
        this.iDirectClient = iDirectClient;
    }

    @Override
    public void sendCodeToPhone(String code, String phone) {
        iDirectClient.send(
                IDirectMessageRequest.builder()
                        .channelType(VOICECODE)
                        .senderName(VOICECODE)
                        .destination(phone)
                        .content(
                                Content.builder()
                                        .contentType("text")
                                        .text("Код авторизации: " + String.join(" ", code.split("")))
                                .build())
                        .build()
        );
    }
}
