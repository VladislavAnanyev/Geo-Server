package com.example.mywebquizengine.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkSchedule {

    @Scheduled(cron = "0 12 * * * ?")
    public void send() {

        System.out.println("dsfsdaf");
    }

}
