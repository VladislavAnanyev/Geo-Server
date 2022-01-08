package com.example.mywebquizengine.model.rabbit;

import javax.validation.Valid;

public class RabbitMessage<T> {

    private Type type;


    private T payload;

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

}
