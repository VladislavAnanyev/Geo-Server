package com.example.meetings;

import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;


@Component
public class Demo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Transactional
    @Override
    public void run(String... args) {

        boolean enabled = false;

        if (!enabled) {
            return;
        }

        List<User> users = userRepository.findAll();
        for (User user : users) {
            rabbitAdmin.deleteQueue(user.getUsername());
        }

        if (rabbitAdmin.getQueueProperties("application") == null) {

            List<User> userList = userRepository.findAll();
            for (User user : userList) {
                for (int i = 0; i < user.getPhotos().size(); i++) {
                    user.getPhotos().get(i).setPosition(i);
                }
                rabbitAdmin.declareExchange(new FanoutExchange(user.getUsername(), true, false));
                Queue queue = new Queue(user.getUsername(), true, false, false);

                Binding binding = new Binding(user.getUsername(), Binding.DestinationType.QUEUE,
                        "message-exchange", user.getUsername(), null);

                rabbitAdmin.declareQueue(queue);
                rabbitAdmin.declareBinding(binding);
            }
        }
    }

}
