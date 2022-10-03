package com.example.mywebquizengine.common.rabbit;

public interface EventProcessor {
    void process(RealTimeEvent type, Long userId);
    Type myType();
}
