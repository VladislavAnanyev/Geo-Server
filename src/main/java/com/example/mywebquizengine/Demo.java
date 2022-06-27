package com.example.mywebquizengine;

import com.example.mywebquizengine.user.repository.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
public class Demo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Transactional
    @Override
    public void run(String... args) {

        /*List<User> users2 = userRepository.findAll();

        for (User user : users2) {
            rabbitAdmin.deleteQueue(user.getUsername());

        }*/

        //if (rabbitAdmin.getQueueProperties("application") == null) {

          /*  List<User> users = userRepository.findAll();

            for (User user : users) {

                for (int i = 0; i < user.getPhotos().size(); i++) {
                    user.getPhotos().get(i).setPosition(i);
                }*/
                //rabbitAdmin.declareExchange(new FanoutExchange(user.getUsername(), true, false));
                /*Queue queue = new Queue(user.getUsername(), true, false, false);

                Binding binding = new Binding(user.getUsername(), Binding.DestinationType.QUEUE,
                        "message-exchange", user.getUsername(), null);

                rabbitAdmin.declareQueue(queue);
                rabbitAdmin.declareBinding(binding);*/


           // }
        //}
//

    }
}
