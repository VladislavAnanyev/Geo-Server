package com.example.mywebquizengine.model.chat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SendMessageRequest {

    @NotNull
    private Long dialogId;

    @NotNull
    @NotBlank
    private String content;

    @NotNull
    @NotBlank
    private String uniqueCode;

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
