package com.example.mywebquizengine.service.chat;

import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.service.utils.RabbitUtil;
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
     * @param exchanges список из имён обменов
     * @param message   сообщение для отправки
     */
    public <T> void send(List<String> exchanges, RealTimeEvent<T> message) {
        for (String destination : exchanges) {
            send(destination, message);
        }
    }

    /**
     * Отправляет сообщение во все связанные с каждым обменом очереди
     *
     * @param userIds            список из имён идентификаторов пользователей, которые получат сообщение
     * @param event              сообщение для отправки
     * @param guaranteedDelivery доставить, даже если получатель не был в этот момент в сети (после появления)
     */
    public <T> void send(List<Long> userIds, RealTimeEvent<T> event, boolean guaranteedDelivery) {
        MessagePostProcessor messagePostProcessor = message -> message;
        if (!guaranteedDelivery) {
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
