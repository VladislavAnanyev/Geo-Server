package com.example.mywebquizengine.common.rabbit;

import com.example.mywebquizengine.chat.model.dto.input.TypingRequest;
import com.example.mywebquizengine.chat.facade.MessageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TypingEventHandler implements EventProcessor {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MessageFacade messageFacade;

    @Override
    public void process(RealTimeEvent realTimeEvent, Long userId) {
        TypingRequest typing = objectMapper.convertValue(realTimeEvent.getPayload(), TypingRequest.class);
        messageFacade.typingMessage(typing.getDialogId(), typing.getUserId());
    }

    @Override
    public Type myType() {
        return MessageType.TYPING;
    }
}
