package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.rabbit.Type;
import com.example.mywebquizengine.model.userinfo.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class RealTimeEventSender {

    @Autowired
    private RabbitSender rabbitSender;

    public void send(Object object, Set<User> users, Type messageOperation) {
        RealTimeEvent<Object> realTimeEvent = new RealTimeEvent<>();
        realTimeEvent.setType(messageOperation);
        realTimeEvent.setPayload(object);

        List<Long> userIds = new ArrayList<>();
        users.stream().map(User::getUserId).forEach(userIds::add);

        boolean guaranteeDelivery = !messageOperation.equals(MessageType.TYPING);
        rabbitSender.send(userIds, realTimeEvent, guaranteeDelivery);
    }
}
