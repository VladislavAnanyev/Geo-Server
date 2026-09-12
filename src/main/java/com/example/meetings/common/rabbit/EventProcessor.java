package com.example.meetings.common.rabbit;

import com.example.meetings.common.rabbit.eventtype.Type;

public interface EventProcessor {
    void process(RealTimeEvent<?> type, Long userId);
    Type myType();
}
