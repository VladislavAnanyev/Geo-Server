package com.example.mywebquizengine.chat.controller;

import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.common.exception.AuthorizationException;
import com.example.mywebquizengine.common.exception.GlobalErrorCode;
import com.example.mywebquizengine.common.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.eventtype.Type;
import com.example.mywebquizengine.common.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Map;

import static com.example.mywebquizengine.common.utils.AuthenticationUtil.setAuthentication;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;
import static org.springframework.security.core.context.SecurityContextHolder.*;

@Controller
@EnableRabbit
@Validated
@Slf4j
public class RabbitController {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private Map<Type, EventProcessor> map;

    @RabbitListener(queues = "incoming-messages", ackMode = "MANUAL")
    public void sendMessageFromAMQPClient(org.springframework.amqp.core.Message messageFromRabbit, Channel channel,
                                          @Header(AmqpHeaders.DELIVERY_TAG) Long tag) throws IOException {
        channel.basicAck(tag, false);
        String authorizationToken = (String) messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (!isAuthenticate(authorizationToken)) {
            throw new AuthorizationException("Unauthorized", GlobalErrorCode.UNAUTHORIZED);
        }

        RealTimeEvent<?> realTimeEvent = objectMapper.readValue(messageFromRabbit.getBody(), RealTimeEvent.class);
        EventProcessor eventProcessor = map.get(realTimeEvent.getType());
        eventProcessor.process(
                realTimeEvent,
                ((AuthUserDetails) getContext().getAuthentication().getPrincipal()).getUserId()
        );
    }

    private boolean isAuthenticate(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Jwt токен не передан");
        }

        Long userId = jwtUtil.extractUserId(token);
        if (userId == null) {
            return false;
        }

        setAuthentication(
                new AuthUserDetails().setUserId(userId),
                commaSeparatedStringToAuthorityList(jwtUtil.extractAuthorities(token))
        );

        return true;
    }

}
