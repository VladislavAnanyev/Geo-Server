package com.example.meetings.integration.idirect;

import com.example.meetings.common.service.CodeSenderService;
import com.example.meetings.integration.idirect.dto.IDirectMessageRequest;
import com.example.meetings.integration.idirect.dto.IDirectMessageRequest.Content;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import static com.example.meetings.util.Constants.IDIRECT_VOICE_CODE;

@Service
@ConditionalOnProperty(value = "codesender.type", havingValue = "idirect")
public class IDirectSenderService implements CodeSenderService {

    private final IDirectClient iDirectClient;

    public IDirectSenderService(IDirectClient iDirectClient) {
        this.iDirectClient = iDirectClient;
    }

    @Override
    public void sendCodeToPhone(String code, String phone) {
        iDirectClient.send(
                IDirectMessageRequest.builder()
                        .channelType(IDIRECT_VOICE_CODE)
                        .senderName(IDIRECT_VOICE_CODE)
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
