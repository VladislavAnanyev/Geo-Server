package com.example.meetings.integration.ollama;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class OllamaGenerateRequest {
    private String model;
    private String prompt;
    private boolean stream;
    private String format;
    private OllamaOptions options;

    @Data
    @Accessors(chain = true)
    public static class OllamaOptions {
        private double temperature;
        @JsonProperty("num_predict")
        private int numPredict;
    }
}
