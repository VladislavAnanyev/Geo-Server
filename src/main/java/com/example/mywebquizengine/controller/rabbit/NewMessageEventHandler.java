package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.dto.input.SendMessageRequest;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.rabbit.Type;
import com.example.mywebquizengine.service.MessageFacade;
import com.example.mywebquizengine.service.model.SendMessageModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NewMessageEventHandler implements EventProcessor {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageFacade messageFacade;

    @Override
    public void process(RealTimeEvent realTimeEvent, Long userId) {
        SendMessageRequest sendMessageRequest = objectMapper
                .convertValue(
                        realTimeEvent.getPayload(),
                        SendMessageRequest.class
                );

        SendMessageModel sendMessageModel = new SendMessageModel();
        sendMessageModel.setContent(sendMessageRequest.getContent());
        sendMessageModel.setDialogId(sendMessageRequest.getDialogId());
        sendMessageModel.setUniqueCode(sendMessageRequest.getUniqueCode());
        sendMessageModel.setSenderId(userId);

        messageFacade.sendMessage(sendMessageModel, userId);
    }

    @Override
    public Type myType() {
        return MessageType.MESSAGE;
    }
}
