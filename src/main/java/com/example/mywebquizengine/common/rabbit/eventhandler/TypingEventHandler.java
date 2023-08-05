package com.example.mywebquizengine.common.rabbit.eventhandler;

import com.example.mywebquizengine.chat.model.dto.input.TypingRequest;
import com.example.mywebquizengine.common.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.eventtype.MessageType;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.eventtype.Type;
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
