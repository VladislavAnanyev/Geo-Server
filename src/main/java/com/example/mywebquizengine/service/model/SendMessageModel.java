package com.example.mywebquizengine.service.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SendMessageModel {
    private Long senderId;
    @NotNull
    private Long dialogId;
    @NotNull
    @NotBlank
    private String content;
    @NotNull
    @NotBlank
    private String uniqueCode;

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }

    public String getUniqueCode() {
        return uniqueCode;
    }

    public void setUniqueCode(String uniqueCode) {
        this.uniqueCode = uniqueCode;
    }
}
