package com.example.mywebquizengine.config;

import com.example.mywebquizengine.controller.rabbit.EventProcessor;
import com.example.mywebquizengine.common.rabbit.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Configuration
public class StrategyConfig {

    @Bean
    public Map<Type, EventProcessor> map(List<EventProcessor> processors) {
        return processors.stream().collect(toMap(EventProcessor::myType, Function.identity()));
    }

}
