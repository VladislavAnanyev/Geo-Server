package com.example.mywebquizengine.common.rabbit;

import com.example.mywebquizengine.common.rabbit.eventtype.Type;

public interface EventProcessor {
    void process(RealTimeEvent<?> type, Long userId);
    Type myType();
}
