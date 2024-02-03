package com.example.meetings.chat.controller;

import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.exception.AuthorizationException;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.rabbit.EventProcessor;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.rabbit.eventtype.Type;
import com.example.meetings.common.utils.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Map;

import static com.example.meetings.common.utils.AuthenticationUtil.setAuthentication;
import static java.util.Optional.ofNullable;
import static org.springframework.security.core.authority.AuthorityUtils.commaSeparatedStringToAuthorityList;
import static org.springframework.security.core.context.SecurityContextHolder.getContext;

@Controller
@EnableRabbit
@Validated
@Slf4j
@RequiredArgsConstructor
public class RabbitController {

    private final ObjectMapper objectMapper;
    private final JWTUtil jwtUtil;
    private final Map<Type, EventProcessor> events;

    @RabbitListener(queues = "incoming-messages")
    public void sendMessageFromAMQPClient(org.springframework.amqp.core.Message messageFromRabbit) throws IOException {
        String authorizationToken = (String) messageFromRabbit.getMessageProperties().getHeaders().get("Authorization");
        if (!isAuthenticate(authorizationToken)) {
            throw new AuthorizationException("Unauthorized", GlobalErrorCode.UNAUTHORIZED);
        }

        RealTimeEvent<?> realTimeEvent = objectMapper.readValue(messageFromRabbit.getBody(), RealTimeEvent.class);
        EventProcessor eventProcessor = events.get(realTimeEvent.getType());
        ofNullable(eventProcessor).ifPresent(
                processor -> processor.process(
                        realTimeEvent,
                        ((AuthUserDetails) getContext().getAuthentication().getPrincipal()).getUserId()
                )
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
