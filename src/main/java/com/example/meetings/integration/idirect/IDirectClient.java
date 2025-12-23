package com.example.meetings.integration.idirect;

import com.example.meetings.integration.idirect.dto.IDirectMessageRequest;
import com.example.meetings.integration.idirect.dto.IDirectMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.springframework.http.HttpMethod.POST;

@Component
@Slf4j
public class IDirectClient {

    private final RestTemplate restTemplate;
    @Value("${idirect.code}")
    private String code;

    public IDirectClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(IDirectMessageRequest request) {
        try {
            restTemplate.exchange(
                    "https://direct.i-dgtl.ru/api/v1/message",
                    POST,
                    new HttpEntity<>(singletonList(request), getHeaders()),
                    IDirectMessageResponse.class
            );
        } catch (Exception e) {
            log.error("Ошибка при отправке кода авторизации", e);
            throw new IllegalStateException("Ошибка при отправке кода авторизации");
        }
    }

    private HttpHeaders getHeaders() {
        return new HttpHeaders(
                new LinkedMultiValueMap<>(
                        Map.of(
                                "Authorization", List.of(code),
                                HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_JSON_VALUE)
                        )
                )
        );
    }
}
