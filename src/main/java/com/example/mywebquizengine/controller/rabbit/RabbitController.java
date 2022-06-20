package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.domain.Dialog;
import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.chat.domain.MessagePhoto;
import com.example.mywebquizengine.model.chat.dto.input.SendMessageRequest;
import com.example.mywebquizengine.model.chat.dto.input.Typing;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.FileSystemStorageService;
import com.example.mywebquizengine.service.model.SendMessageModel;
import com.example.mywebquizengine.service.utils.JWTUtil;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

@Controller
@EnableRabbit
@Validated
public class RabbitController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    private Long userId;

    @RabbitListener(queues = "incoming-messages", ackMode = "MANUAL")
    public void sendMessageFromAMQPClient(org.springframework.amqp.core.Message messageFromRabbit, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException {
        channel.basicAck(tag, false);
        Object authorizationToken = messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (!isAuthenticate(authorizationToken.toString())) {
            System.out.println("Неаутентифицированный запрос");
            return;
        }

        RealTimeEvent realTimeEvent = objectMapper.readValue(messageFromRabbit.getBody(), RealTimeEvent.class);

        if (realTimeEvent.getType().equals(MessageType.MESSAGE)) {
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
            messageService.sendMessage(sendMessageModel);
        } else if (realTimeEvent.getType().equals(MessageType.TYPING)) {
            Typing typing = objectMapper.convertValue(realTimeEvent.getPayload(), Typing.class);
            messageService.typingMessage(typing.getDialogId(), typing.getUser().getUserId());
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private boolean isAuthenticate(String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userId = jwtUtil.extractUserId(token);
        return userId != null;
    }

}
