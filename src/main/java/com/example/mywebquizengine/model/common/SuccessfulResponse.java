package com.example.mywebquizengine.model.common;

public class SuccessfulResponse implements Response {
    public ResponseStatus getStatus() {
        return ResponseStatus.SUCCESS;
    }
}
