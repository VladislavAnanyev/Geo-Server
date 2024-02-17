package com.example.meetings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@Configuration
public class ProjectionConfig {
    @Bean
    public SpelAwareProxyProjectionFactory projectionFactory() {
        return new SpelAwareProxyProjectionFactory();
    }
}
