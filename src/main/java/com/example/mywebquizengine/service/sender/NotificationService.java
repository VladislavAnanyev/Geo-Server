package com.example.mywebquizengine.service.sender;

import com.example.mywebquizengine.model.rabbit.MessageType;
import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.rabbit.Type;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.service.chat.RabbitSender;
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

        boolean guaranteeDelivery = !type.equals(MessageType.TYPING);
        rabbitSender.send(userIds, realTimeEvent, guaranteeDelivery);
    }
}
