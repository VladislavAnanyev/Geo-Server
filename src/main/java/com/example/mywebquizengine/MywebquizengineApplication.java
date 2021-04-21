package com.example.mywebquizengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
public class MywebquizengineApplication {

    public static void main(String[] args) throws IOException {
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
