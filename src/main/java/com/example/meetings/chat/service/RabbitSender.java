package com.example.meetings.chat.service;

import com.example.meetings.common.rabbit.eventtype.MessageType;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.utils.RabbitUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONValue;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RabbitSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Отправляет сообщение во все связанные с обменом очереди
     *
     * @param exchange имя обмена
     * @param message  сообщение для отправки
     */
    public <T> void send(String exchange, RealTimeEvent<T> message) {
        try {
            rabbitTemplate.convertAndSend(exchange,
                    "",
                    JSONValue.parse(objectMapper.writeValueAsString(message)));
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Отправляет сообщение во все связанные с обменом очереди
     *
     * @param exchange             имя обмена
     * @param message              сообщение для отправки
     * @param messagePostProcessor объект, в котором хранятся метаданные сообщения (если требуются)
     */
    public <T> void send(String exchange, RealTimeEvent<T> message, MessagePostProcessor messagePostProcessor) {
        try {
            rabbitTemplate.convertAndSend(exchange,
                    "",
                    JSONValue.parse(objectMapper.writeValueAsString(message)),
                    messagePostProcessor);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Отправляет сообщение во все связанные с каждым обменом очереди
     *
     * @param userIds            список из имён идентификаторов пользователей, которые получат сообщение
     * @param event              сообщение для отправки
     */
    public <T> void send(List<Long> userIds, RealTimeEvent<T> event) {

        MessagePostProcessor messagePostProcessor = message -> message;
        if (event.getType().equals(MessageType.TYPING)) {
            messagePostProcessor = message -> {
                message.getMessageProperties().setExpiration(String.valueOf(0));
                return message;
            };
        }

        for (Long userId : userIds) {
            send(RabbitUtil.getExchangeName(userId), event, messagePostProcessor);
        }
    }

}
