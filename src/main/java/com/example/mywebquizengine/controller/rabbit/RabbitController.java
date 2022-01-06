package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.rabbit.Typing;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.simple.parser.ParseException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;

@Controller
@EnableRabbit
@Validated
public class RabbitController {

    @Autowired
    private MessageService messageService;

    @MessageMapping("/user/{dialogId}")
    public void sendMessage(@Valid @Payload Message message,
                            @AuthenticationPrincipal Principal principal
    ) throws JsonProcessingException, ParseException {
        if (message.getSender().getUsername().equals(principal.getName())) {
            messageService.sendMessage(message);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
    }

    @RabbitListener(queues = "incoming-messages")
    public void sendMessageFromAMQPClient(@Valid Message message) throws JsonProcessingException, ParseException {
        messageService.sendMessage(message);
    }

    @RabbitListener(queues = "typing")
    public void typing(Typing typing) throws JsonProcessingException, ParseException {
        messageService.typingMessage(typing);
    }

}
