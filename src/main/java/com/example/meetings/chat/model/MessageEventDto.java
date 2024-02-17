package com.example.meetings.chat.model;

import com.example.meetings.chat.model.domain.MessageStatus;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class MessageEventDto {
    private Long dialogId;
    private MessageStatus status;
}
