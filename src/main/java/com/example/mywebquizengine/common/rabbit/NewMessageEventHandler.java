package com.example.mywebquizengine.common.rabbit;

import com.example.mywebquizengine.chat.facade.MessageFacade;
import com.example.mywebquizengine.chat.model.ForwardedMessages;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.dto.input.SendMessageRequest;
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
        SendMessageRequest sendMessageRequest = objectMapper.convertValue(
                realTimeEvent.getPayload(),
                SendMessageRequest.class
        );

        SendMessageModel sendMessageModel = new SendMessageModel()
                .setContent(sendMessageRequest.getContent())
                .setDialogId(sendMessageRequest.getDialogId())
                .setUniqueCode(sendMessageRequest.getUniqueCode())
                .setSenderId(userId)
                .setFiles(sendMessageRequest.getFiles());

        if (sendMessageRequest.getForwardedMessagesRequest() != null) {
            ForwardedMessages forwardedMessages = new ForwardedMessages()
                    .setDialogId(sendMessageRequest.getDialogId())
                    .setMessagesId(sendMessageRequest.getForwardedMessagesRequest().getMessagesId());
            sendMessageModel.setForwardedMessages(forwardedMessages);
        }

        messageFacade.sendMessage(sendMessageModel);
    }

    @Override
    public Type myType() {
        return MessageType.MESSAGE;
    }
}
