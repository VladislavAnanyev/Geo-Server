package com.example.meetings.common.rabbit.eventhandler;

import com.example.meetings.chat.model.dto.input.TypingRequest;
import com.example.meetings.common.rabbit.EventProcessor;
import com.example.meetings.common.rabbit.eventtype.MessageType;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.rabbit.eventtype.Type;
import org.springframework.stereotype.Service;

@Service
public class TypingEventHandler extends CommonEventHandler implements EventProcessor {

    @Override
    public void process(RealTimeEvent<?> realTimeEvent, Long userId) {
        TypingRequest typing = objectMapper.convertValue(realTimeEvent.getPayload(), TypingRequest.class);
        messageFacade.typingMessage(typing.getDialogId(), typing.getUserId());
    }

    @Override
    public Type myType() {
        return MessageType.TYPING;
    }
}
