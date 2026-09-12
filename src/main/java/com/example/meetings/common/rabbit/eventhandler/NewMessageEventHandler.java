package com.example.meetings.common.rabbit.eventhandler;

import com.example.meetings.chat.model.ForwardedMessages;
import com.example.meetings.chat.model.SendMessageModel;
import com.example.meetings.chat.model.dto.input.SendMessageRequest;
import com.example.meetings.common.rabbit.EventProcessor;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.rabbit.eventtype.MessageType;
import com.example.meetings.common.rabbit.eventtype.Type;
import org.springframework.stereotype.Service;

import static java.util.Objects.isNull;

@Service
public class NewMessageEventHandler extends CommonEventHandler implements EventProcessor {

    @Override
    public void process(RealTimeEvent<?> realTimeEvent, Long userId) {
        SendMessageRequest request = objectMapper.convertValue(
                realTimeEvent.getPayload(),
                SendMessageRequest.class
        );

        SendMessageModel sendMessageModel = new SendMessageModel()
                .setContent(request.getContent())
                .setDialogId(request.getDialogId())
                .setUniqueCode(request.getUniqueCode())
                .setSenderId(userId)
                .setFiles(request.getFiles());

        if (!isNull(request.getForwardedMessagesRequest())) {
            sendMessageModel.setForwardedMessages(
                    new ForwardedMessages()
                            .setDialogId(request.getDialogId())
                            .setMessagesId(request.getForwardedMessagesRequest().getMessagesId())
            );
        }

        messageFacade.sendMessage(sendMessageModel);
    }

    @Override
    public Type myType() {
        return MessageType.MESSAGE;
    }
}
