package com.example.mywebquizengine.common.rabbit.eventhandler;

import com.example.mywebquizengine.common.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.eventtype.MessageType;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.ReceiveMessageRequest;
import com.example.mywebquizengine.common.rabbit.eventtype.Type;
import org.springframework.stereotype.Service;

@Service
public class ChangeStatusEventHandler extends CommonEventHandler implements EventProcessor {

    @Override
    public void process(RealTimeEvent<?> realTimeEvent, Long userId) {
        ReceiveMessageRequest receiveMessageRequest = objectMapper.convertValue(
                realTimeEvent.getPayload(),
                ReceiveMessageRequest.class
        );

        messageFacade.receiveMessages(userId, receiveMessageRequest.getDialogId());
    }

    @Override
    public Type myType() {
        return MessageType.CHANGE_STATUS;
    }
}
