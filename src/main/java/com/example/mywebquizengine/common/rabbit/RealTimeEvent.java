package com.example.mywebquizengine.common.rabbit;

import com.example.mywebquizengine.common.rabbit.eventtype.Type;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RealTimeEvent<T> {
    private Type type;
    private T payload;
}
