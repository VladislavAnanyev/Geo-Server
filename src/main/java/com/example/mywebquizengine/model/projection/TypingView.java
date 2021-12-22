package com.example.mywebquizengine.model.projection;

import org.springframework.beans.factory.annotation.Value;

public interface TypingView {
    UserCommonView getSender();

    @Value("#{target.dialog.dialogId}")
    Long getDialogId();

    String getType();
}
