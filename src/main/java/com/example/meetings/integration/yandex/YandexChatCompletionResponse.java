package com.example.meetings.integration.yandex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class YandexChatCompletionResponse {

    private List<Choice> choices;

    @Data
    public static class Choice {
        private YandexChatCompletionRequest.YandexChatMessage message;
        @JsonProperty("finish_reason")
        private String finishReason;
    }
}
