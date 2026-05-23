package com.example.meetings.integration.ollama;

import com.example.meetings.integration.moderation.ModerationLlmClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.example.meetings.util.Constants.MODERATION_LLM_MAX_TOKENS;
import static com.example.meetings.util.Constants.MODERATION_TEMPERATURE;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@ConditionalOnExpression("${text.moderation.enabled:false} && '${text.moderation.provider:}'.equals('ollama')")
@Slf4j
public class OllamaClient implements ModerationLlmClient {

    private static final String GENERATE_PATH = "/api/generate";
    private static final String RESPONSE_FORMAT_JSON = "json";

    private final RestTemplate restTemplate;

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:qwen2.5:3b-instruct}")
    private String model;

    public OllamaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(@NonNull String prompt) {
        OllamaGenerateRequest request = new OllamaGenerateRequest()
                .setModel(model)
                .setStream(false)
                .setFormat(RESPONSE_FORMAT_JSON)
                .setPrompt(prompt)
                .setOptions(new OllamaGenerateRequest.OllamaOptions()
                        .setTemperature(MODERATION_TEMPERATURE)
                        .setNumPredict(MODERATION_LLM_MAX_TOKENS));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        HttpEntity<OllamaGenerateRequest> httpEntity = new HttpEntity<>(request, headers);

        log.debug("Ollama generate request to {}", baseUrl + GENERATE_PATH);
        ResponseEntity<OllamaGenerateResponse> response = restTemplate.exchange(
                baseUrl + GENERATE_PATH,
                POST,
                httpEntity,
                OllamaGenerateResponse.class
        );
        OllamaGenerateResponse body = response.getBody();
        return body != null ? body.getResponse() : null;
    }
}
