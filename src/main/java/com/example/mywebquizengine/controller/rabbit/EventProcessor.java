package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.rabbit.RealTimeEvent;
import com.example.mywebquizengine.model.rabbit.Type;

public interface EventProcessor {
    void process(RealTimeEvent type, Long userId);
    Type myType();
}
