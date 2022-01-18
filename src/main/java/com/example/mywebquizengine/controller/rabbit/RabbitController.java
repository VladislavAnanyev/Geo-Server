package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.chat.SendMessageRequest;
import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.model.chat.Typing;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
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
    ) throws JsonProcessingException, IllegalAccessException {
        if (rabbitMessage.getType().equals(MessageType.MESSAGE)) {
            SendMessageRequest sendMessageRequest = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), SendMessageRequest.class);

            Message message = new Message();
            message.setContent(sendMessageRequest.getContent());

            User user = new User();
            user.setUsername(principal.getName());
            message.setSender(user);

            Dialog dialog = new Dialog();
            dialog.setDialogId(sendMessageRequest.getDialogId());
            message.setDialog(dialog);
            message.setUniqueCode(sendMessageRequest.getUniqueCode());

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
    public void sendMessageFromAMQPClient(@Valid RabbitMessage rabbitMessage, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException, IllegalAccessException {
        channel.basicAck(tag, false);
        if (rabbitMessage.getType().equals(MessageType.MESSAGE)) {
            SendMessageRequest sendMessageRequest = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), SendMessageRequest.class);


            Message message = new Message();
            message.setContent(sendMessageRequest.getContent());

            User user = new User();
            user.setUsername(sendMessageRequest.getUsername());
            message.setSender(user);

            Dialog dialog = new Dialog();
            dialog.setDialogId(sendMessageRequest.getDialogId());
            message.setDialog(dialog);
            message.setUniqueCode(sendMessageRequest.getUniqueCode());

            messageService.sendMessage(message);
        } else if (rabbitMessage.getType().equals(MessageType.TYPING)) {
            Typing typing = objectMapper.readValue(objectMapper
                    .writeValueAsString(rabbitMessage.getPayload()), Typing.class);
            messageService.typingMessage(typing);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

}
