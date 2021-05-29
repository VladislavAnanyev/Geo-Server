package com.example.mywebquizengine;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.net.InetAddress;

@SpringBootApplication
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
}
