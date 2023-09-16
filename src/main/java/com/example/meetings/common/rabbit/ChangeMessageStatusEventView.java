package com.example.meetings.common.rabbit;

import com.example.meetings.chat.model.domain.MessageStatus;
import org.springframework.beans.factory.annotation.Value;

public interface ChangeMessageStatusEventView {
    @Value("#{target.dialog.dialogId}")
    Long getDialogId();
    MessageStatus getStatus();
}
