package com.example.mywebquizengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.InetAddress;

@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MywebquizengineApplication {

    public static void main(String[] args) {
        SpringApplication.run(MywebquizengineApplication.class, args);
    }

    /*@Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }*/

    /*@Bean
    public RedisConnectionFactory factory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }*/

    public static ApplicationContext ctx;

    /**
     * Make Spring inject the application context
     * and save it on a static variable,
     * so that it can be accessed from any point in the application.
     */
    @Autowired
    private void setApplicationContext(ApplicationContext applicationContext) {
        ctx = applicationContext;
    }
}
