package com.example.mywebquizengine.request.model.dto.input;

import javax.validation.constraints.NotNull;

public class RequestDto {

    @NotNull
    private Long toUserId;

    @NotNull
    private Long meetingId;

    private String messageContent;

    public Long getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(Long meetingId) {
        this.meetingId = meetingId;
    }

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
