package com.example.meetings.chat.model.dto.output;

import org.springframework.beans.factory.annotation.Value;

public interface TypingView {
    @Value("#{target.user.username}")
    String getUsername();

    @Value("#{target.user.firstName}")
    String getFirstName();

    @Value("#{target.user.lastName}")
    String getLastName();

    @Value("#{target.dialogId}")
    Long getDialogId();
}
