package com.example.meetings.common.service;

import com.example.meetings.chat.service.RabbitSender;
import com.example.meetings.common.rabbit.RealTimeEvent;
import com.example.meetings.common.rabbit.eventtype.Type;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EventService {

    @Autowired
    private RabbitSender rabbitSender;

    public void send(Object object, Set<User> users, Type type) {
        RealTimeEvent<Object> realTimeEvent = new RealTimeEvent<>();
        realTimeEvent.setType(type);
        realTimeEvent.setPayload(object);

        List<Long> userIds = new ArrayList<>();
        users.stream().map(User::getUserId).forEach(userIds::add);

        rabbitSender.send(userIds, realTimeEvent);
    }
}
