package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.*;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.FileSystemStorageService;
import com.example.mywebquizengine.service.JWTUtil;
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

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
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

    @Autowired
    private FileSystemStorageService fileSystemStorageService;


    @RabbitListener(queues = "incoming-messages", ackMode = "MANUAL")
    public void sendMessageFromAMQPClient(org.springframework.amqp.core.Message messageFromRabbit, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException {
        channel.basicAck(tag, false);
        Object authorization = messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (authorization == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        String accessKey = authorization.toString();
        String username = jwtUtil.extractUsername(accessKey);

        if (username != null) {
            RabbitMessage rabbitMessage = objectMapper.readValue(messageFromRabbit.getBody(), RabbitMessage.class);
            if (rabbitMessage.getType().equals(MessageType.MESSAGE)) {
                SendMessageRequest sendMessageRequest = objectMapper.readValue(objectMapper
                        .writeValueAsString(rabbitMessage.getPayload()), SendMessageRequest.class);

                Message message = new Message();
                message.setContent(sendMessageRequest.getContent());

                User user = new User();
                user.setUsername(username);
                message.setSender(user);

                Dialog dialog = new Dialog();
                dialog.setDialogId(sendMessageRequest.getDialogId());
                message.setDialog(dialog);
                message.setUniqueCode(sendMessageRequest.getUniqueCode());

                String photoUrl = sendMessageRequest.getPhotoUrl();
                if (photoUrl != null) {

                    String filename = photoUrl.substring(photoUrl.lastIndexOf("/") + 1);
                    Path path = fileSystemStorageService.load(filename);
                    if (Files.exists(path)) {
                        MessagePhoto messagePhoto = new MessagePhoto();
                        messagePhoto.setPhoto_url(sendMessageRequest.getPhotoUrl());
                        message.setPhotos(Collections.singletonList(messagePhoto));
                    }

                }

                messageService.sendMessage(message);
            } else if (rabbitMessage.getType().equals(MessageType.TYPING)) {
                Typing typing = objectMapper.readValue(objectMapper
                        .writeValueAsString(rabbitMessage.getPayload()), Typing.class);
                User user = new User();
                user.setUsername(username);
                typing.setUser(user);
                messageService.typingMessage(typing);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }


}
