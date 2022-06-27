package com.example.mywebquizengine.common;

import com.example.mywebquizengine.common.rabbit.MessageType;
import com.example.mywebquizengine.common.rabbit.RealTimeEvent;
import com.example.mywebquizengine.common.rabbit.Type;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.chat.service.RabbitSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class NotificationService {

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
