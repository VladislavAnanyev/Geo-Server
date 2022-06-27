package com.example.mywebquizengine.common.exception;

public class UserNotFoundException extends GlobalException {

    public UserNotFoundException(String message, Long code) {
        super(message, code);
    }
}
