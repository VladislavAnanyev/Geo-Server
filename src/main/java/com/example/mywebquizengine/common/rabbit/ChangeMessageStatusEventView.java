package com.example.mywebquizengine.common.rabbit;

import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import org.springframework.beans.factory.annotation.Value;

public interface ChangeMessageStatusEventView {
    @Value("#{target.dialog.dialogId}")
    Long getDialogId();
    MessageStatus getStatus();
}
