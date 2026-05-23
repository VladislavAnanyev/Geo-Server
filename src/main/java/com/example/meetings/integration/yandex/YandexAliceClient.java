package com.example.meetings.integration.yandex;

import com.example.meetings.integration.moderation.ModerationLlmClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

import static com.example.meetings.util.Constants.MODERATION_LLM_MAX_TOKENS;
import static com.example.meetings.util.Constants.MODERATION_TEMPERATURE;
import static com.example.meetings.util.Constants.YANDEX_CHAT_COMPLETIONS_PATH;
import static com.example.meetings.util.Constants.YANDEX_CONTENT_FILTER_REJECTED_JSON;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
@ConditionalOnExpression("${text.moderation.enabled:false} && '${text.moderation.provider:}'.equals('yandex')")
@Slf4j
public class YandexAliceClient implements ModerationLlmClient {

    private static final String FINISH_REASON_CONTENT_FILTER = "content_filter";

    private final RestTemplate restTemplate;

    @Value("${yandex.ai.base-url:https://llm.api.cloud.yandex.net/v1}")
    private String baseUrl;

    @Value("${yandex.ai.api-key:}")
    private String apiKey;

    @Value("${yandex.ai.folder-id:}")
    private String folderId;

    @Value("${yandex.ai.model:aliceai-llm-flash}")
    private String model;

    public YandexAliceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public String generate(@NonNull String prompt) {
        YandexChatCompletionRequest request = new YandexChatCompletionRequest()
                .setModel(resolveModelUri())
                .setTemperature(MODERATION_TEMPERATURE)
                .setMaxTokens(MODERATION_LLM_MAX_TOKENS)
                .setResponseFormat(moderationResponseFormat())
                .setMessages(List.of(
                        new YandexChatCompletionRequest.YandexChatMessage()
                                .setRole("user")
                                .setContent(prompt)
                ));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        headers.set("Authorization", "Api-Key " + apiKey);
        if (StringUtils.isNotBlank(folderId)) {
            headers.set("x-folder-id", folderId);
        }
        headers.set("x-data-logging-enabled", "false");

        HttpEntity<YandexChatCompletionRequest> httpEntity = new HttpEntity<>(request, headers);

        log.debug("Yandex Alice generate request to {}", baseUrl + YANDEX_CHAT_COMPLETIONS_PATH);
        var response = restTemplate.exchange(
                baseUrl + YANDEX_CHAT_COMPLETIONS_PATH,
                POST,
                httpEntity,
                YandexChatCompletionResponse.class
        );
        YandexChatCompletionResponse body = response.getBody();
        if (body == null || body.getChoices() == null || body.getChoices().isEmpty()) {
            return null;
        }
        YandexChatCompletionResponse.Choice choice = body.getChoices().get(0);
        if (FINISH_REASON_CONTENT_FILTER.equals(choice.getFinishReason())) {
            log.debug("Yandex guardrails content_filter");
            return YANDEX_CONTENT_FILTER_REJECTED_JSON;
        }
        if (choice.getMessage() == null) {
            return null;
        }
        return choice.getMessage().getContent();
    }

    private static YandexChatCompletionRequest.ResponseFormat moderationResponseFormat() {
        return new YandexChatCompletionRequest.ResponseFormat()
                .setType("json_schema")
                .setJsonSchema(new YandexChatCompletionRequest.JsonSchema()
                        .setName("moderation_result")
                        .setStrict(true)
                        .setSchema(Map.of(
                                "type", "object",
                                "properties", Map.of(
                                        "result", Map.of("type", "boolean")
                                ),
                                "required", List.of("result"),
                                "additionalProperties", false
                        )));
    }

    private String resolveModelUri() {
        if (StringUtils.startsWith(model, "gpt://")) {
            return model;
        }
        return "gpt://" + folderId + "/" + model;
    }
}
