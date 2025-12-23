package com.example.meetings.common.exception;

public class UserNotFoundException extends GlobalException {

    public UserNotFoundException(String message, Long code) {
        super(message, code);
    }
}
