package com.example.meetings.chat.model.dto.output;

import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public interface LastDialog {
    @Value("#{target.dialogId}")
    Long getDialogId();

    String getContent();

    @Value("#{target.status}")
    String getStatus();

    Date getTimestamp();

    Long getUserId();
}
