package com.example.mywebquizengine.model.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class RequestDto {

    @NotNull
    @NotBlank
    private String toUsername;

    @NotNull
    private Long meetingId;

    private String messageContent;

    public Long getMeetingId() {
        return meetingId;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getToUsername() {
        return toUsername;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

    public void setMessageContent(String messageContent) {
        this.messageContent = messageContent;
    }

    public void setToUsername(String toUsername) {
        this.toUsername = toUsername;
    }
}
