package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.chat.facade.MessageFacade;
import com.example.mywebquizengine.common.rabbit.MessageType;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.Type;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChangeStatusEventHandler implements EventProcessor {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageFacade messageFacade;

    @Override
    public void process(RealTimeEvent realTimeEvent, Long userId) {
        ReceiveMessageRequest receiveMessageRequest = objectMapper.convertValue(
                realTimeEvent.getPayload(),
                ReceiveMessageRequest.class
        );
//        constraintsUtil.checkConstraints(receiveMessageRequest);

        messageFacade.receiveMessages(userId, receiveMessageRequest.getDialogId());
    }

    @Override
    public Type myType() {
        return MessageType.CHANGE_STATUS;
    }
}
