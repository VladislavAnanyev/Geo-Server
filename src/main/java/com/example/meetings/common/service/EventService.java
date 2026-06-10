package com.example.meetings.common.service;

import com.example.meetings.chat.service.RabbitSender;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.rabbit.eventtype.Type;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Публикация событий в реальном времени через RabbitMQ для указанных пользователей.
 */
@Service
public class EventService {

    @Autowired
    private RabbitSender rabbitSender;

    /**
     * Отправляет событие заданного типа указанным пользователям.
     *
     * @param object полезная нагрузка события
     * @param users  получатели события
     * @param type   тип события
     */
    public void send(Object object, Set<User> users, Type type) {
        RealTimeEvent<Object> realTimeEvent = new RealTimeEvent<>();
        realTimeEvent.setType(type);
        realTimeEvent.setPayload(object);

        List<Long> userIds = new ArrayList<>();
        users.stream().map(User::getUserId).forEach(userIds::add);

        rabbitSender.send(userIds, realTimeEvent);
    }
}
