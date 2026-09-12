package com.example.meetings.request.model.dto.input;

import javax.validation.constraints.NotNull;

public class RequestDto {

    @NotNull
    private Long toUserId;

    private String messageContent;

    public String getMessageContent() {
        return messageContent;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }
}
