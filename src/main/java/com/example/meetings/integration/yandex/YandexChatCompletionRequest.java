package com.example.meetings.integration.yandex;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class YandexChatCompletionRequest {

    private String model;
    private List<YandexChatMessage> messages;
    private Double temperature;
    @JsonProperty("max_tokens")
    private Integer maxTokens;
    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @Data
    @Accessors(chain = true)
    public static class YandexChatMessage {
        private String role;
        private String content;
    }

    @Data
    @Accessors(chain = true)
    public static class ResponseFormat {
        private String type;
        @JsonProperty("json_schema")
        private JsonSchema jsonSchema;
    }

    @Data
    @Accessors(chain = true)
    public static class JsonSchema {
        private String name;
        private Boolean strict;
        private Map<String, Object> schema;
    }
}
