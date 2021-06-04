package com.example.mywebquizengine.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkSchedule {


    @Autowired
    private MailSender sender;

    @Scheduled(cron = "0 12 * * * ?")
    public void send() {
        sender.send("a.vlad.v@ya.ru","Подписка на WebQuizzes", "Вы подписаны на рассылку сообщений" +
                " от WebQuizzes, это письмо приходит каждый день в 12 часов дня по МСК");
    }

}
