package com.example.meetings.integration.sigmasms;

import com.example.meetings.integration.sigmasms.dto.MessageRequest;
import com.example.meetings.integration.sigmasms.dto.Payload;
import com.example.meetings.integration.sigmasms.dto.SigmaSmsMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static com.example.meetings.integration.sigmasms.dto.SigmaSmsMessageType.TELEGRAM;
import static com.example.meetings.integration.sigmasms.dto.SigmaSmsMessageType.VOICE;
import static org.springframework.http.HttpMethod.POST;

@Component
@RequiredArgsConstructor
public class SigmaSmsClient {

    public static final String AWS_TATYANA = "aws:tatyana";
    public static final String NUMBER = "74956665610";
    private final RestTemplate restTemplate;
    @Value("${sigmasms.code}")
    private String authCode;

    public void voiceCode(String phone, String text) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("Authorization", authCode);
        params.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Payload payload = Payload.builder().tts(AWS_TATYANA).sender(NUMBER).text("Код авторизации: " + String.join(" ", text.split(""))).build();
        MessageRequest messageRequest = MessageRequest.builder().type(VOICE.getValue()).payload(payload).recipient(phone).build();

        restTemplate.exchange(
                "https://user.sigmasms.ru/api/sendings",
                POST,
                new HttpEntity<>(messageRequest, new HttpHeaders(params)),
                String.class
        );
    }

    public void telegramCode(String phone, String text) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("Authorization", authCode);
        params.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        Payload payload = Payload.builder().sender("-").text(text).build();
        MessageRequest messageRequest = MessageRequest.builder().type(TELEGRAM.getValue()).payload(payload).recipient(phone).build();

        restTemplate.exchange(
                "https://user.sigmasms.ru/api/sendings",
                POST,
                new HttpEntity<>(messageRequest, new HttpHeaders(params)),
                String.class
        );
    }

}
