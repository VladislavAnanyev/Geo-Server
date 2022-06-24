package com.example.mywebquizengine.controller.rabbit;

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
