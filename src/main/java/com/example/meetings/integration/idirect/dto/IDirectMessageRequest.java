package com.example.meetings.integration.idirect.dto;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Builder
public class IDirectMessageRequest {
    private String channelType;
    private String senderName;
    private String destination;
    private Content content;

    @Data
    @Builder
    public static class Content {
        private String contentType;
        private String text;
    }
}
