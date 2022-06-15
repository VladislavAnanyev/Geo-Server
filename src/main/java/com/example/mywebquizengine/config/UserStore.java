package com.example.mywebquizengine.config;

import com.example.mywebquizengine.security.ActiveUserStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserStore {
    @Bean
    public ActiveUserStore activeUserStore() {
        return new ActiveUserStore();
    }
}
