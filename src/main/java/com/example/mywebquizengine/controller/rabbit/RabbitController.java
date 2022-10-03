package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.Type;
import com.example.mywebquizengine.common.utils.JWTUtil;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

@Controller
@EnableRabbit
@Validated
public class RabbitController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private Map<Type, EventProcessor> map;

    private Long userId;

    @RabbitListener(queues = "incoming-messages", ackMode = "MANUAL")
    public void sendMessageFromAMQPClient(org.springframework.amqp.core.Message messageFromRabbit, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException {
        System.out.println(LocalDateTime.now());
        System.out.println("Начало");
        channel.basicAck(tag, false);
        Object authorizationToken = messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (!isAuthenticate(authorizationToken.toString())) {
            System.out.println("Неаутентифицированный запрос");
            return;
        }

        RealTimeEvent realTimeEvent = objectMapper.readValue(messageFromRabbit.getBody(), RealTimeEvent.class);
        EventProcessor eventProcessor = map.get(realTimeEvent.getType());
        eventProcessor.process(realTimeEvent, userId);
        System.out.println("Конец");
        System.out.println(LocalDateTime.now());
    }

    private boolean isAuthenticate(String token) {
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        userId = jwtUtil.extractUserId(token);
        return userId != null;
    }

}
