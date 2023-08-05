package com.example.mywebquizengine.common.rabbit.eventhandler;

import com.example.mywebquizengine.chat.model.ForwardedMessages;
import com.example.mywebquizengine.chat.model.SendMessageModel;
import com.example.mywebquizengine.chat.model.dto.input.SendMessageRequest;
import com.example.mywebquizengine.common.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.eventtype.MessageType;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.eventtype.Type;
import org.springframework.stereotype.Service;

@Service
public class NewMessageEventHandler extends CommonEventHandler implements EventProcessor {

    @Override
    public void process(RealTimeEvent<?> realTimeEvent, Long userId) {
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
