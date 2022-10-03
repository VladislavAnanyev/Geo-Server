package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.common.exception.AuthorizationException;
import com.example.mywebquizengine.common.exception.GlobalErrorCode;
import com.example.mywebquizengine.common.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.Type;
import com.example.mywebquizengine.common.utils.AuthenticationUtil;
import com.example.mywebquizengine.common.utils.JWTUtil;
import com.example.mywebquizengine.user.model.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
        Object authorizationToken = messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (!isAuthenticate(authorizationToken.toString())) {
            throw new AuthorizationException("Unauthorized", GlobalErrorCode.UNAUTHORIZED);
        }

        RealTimeEvent realTimeEvent = objectMapper.readValue(messageFromRabbit.getBody(), RealTimeEvent.class);
        EventProcessor eventProcessor = map.get(realTimeEvent.getType());
        eventProcessor.process(
                realTimeEvent,
                ((User) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUserId()
        );
    }

    private boolean isAuthenticate(String token) {
        if (token == null) {
            throw new IllegalArgumentException("Jwt токен не передан");
        }
        Long userId = jwtUtil.extractUserId(token);
        String commaSeparatedListOfAuthorities = jwtUtil.extractAuthorities(token);

        List<GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(commaSeparatedListOfAuthorities);
        User authUser = new User();
        authUser.setUserId(userId);
        AuthenticationUtil.setAuthentication(authUser, authorities);
        return userId != null;
    }

}
