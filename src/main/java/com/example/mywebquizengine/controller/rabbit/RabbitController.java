package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.rabbit.Typing;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.security.Principal;

@Controller
@EnableRabbit
@Validated
public class RabbitController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @MessageMapping("/user/{dialogId}")
    public void sendMessage(@Valid @Payload RabbitMessage rabbitMessage,
                            @AuthenticationPrincipal Principal principal
    ) throws JsonProcessingException {
        if (rabbitMessage.getType().equals(MessageType.MESSAGE)) {
            Message message = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), Message.class);
            if (message.getSender().getUsername().equals(principal.getName())) {
                messageService.sendMessage(message);
            } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        } else if (rabbitMessage.getType().equals(MessageType.TYPING)) {
            Typing typing = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), Typing.class);
            messageService.typingMessage(typing);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @RabbitListener(queues = "incoming-messages")
    public void sendMessageFromAMQPClient(@Valid RabbitMessage rabbitMessage) throws JsonProcessingException {
        if (rabbitMessage.getType().equals(MessageType.MESSAGE)) {
            Message message = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), Message.class);
            messageService.sendMessage(message);
        } else if (rabbitMessage.getType().equals(MessageType.TYPING)) {
            Typing typing = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), Typing.class);
            messageService.typingMessage(typing);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

}
