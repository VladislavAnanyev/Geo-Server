package com.example.mywebquizengine.common.rabbit;

import javax.validation.constraints.NotNull;

public class ReceiveMessageRequest {
    @NotNull
    private Long dialogId;

    public Long getDialogId() {
        return dialogId;
    }

    public void setDialogId(Long dialogId) {
        this.dialogId = dialogId;
    }
}

