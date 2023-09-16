package com.example.meetings.chat.model.dto.input;

public class TypingRequest {
    private Long userId;
    private Long dialogId;

    public Long getDialogId() {
        return dialogId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
