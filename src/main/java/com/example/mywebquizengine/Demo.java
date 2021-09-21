package com.example.mywebquizengine;

import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.SimpleJob;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserQuizAnswerRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.UserAnswerService;
import com.example.mywebquizengine.Service.UserService;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.TriggerBuilder.newTrigger;

@Component
public class Demo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private UserAnswerService userAnswerService;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Autowired
    private UserService userService;

    @Autowired
    private RabbitAdmin rabbitAdmin;




    @Override
    public void run(String... args) throws UnknownHostException, SchedulerException, InterruptedException, NoSuchAlgorithmException {

   /*     List<User> users = userRepository.findAll();


        for (User user: users) {
            Queue queue = new Queue(user.getUsername(), true, false, false);

            Binding binding = new Binding(user.getUsername(), Binding.DestinationType.QUEUE,
                    "message-exchange", user.getUsername(), null);

            rabbitAdmin.declareQueue(queue);
            rabbitAdmin.declareBinding(binding);
        }*/



        //Thread.sleep(5000L);

       // scheduler.shutdown(true);

        //Map<Integer, Double> answerStats = userAnswerService.getAnswerStats();
        /*User user = userRepository.findById("application").get();

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);

        user.setRoles(roles);

        userRepository.save(user);*/
        //answerStats.clear();



    }
}
