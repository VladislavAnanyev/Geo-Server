package com.example.mywebquizengine.model.exception;

public class WrongChangePasswordCodeException extends GlobalException {
    public WrongChangePasswordCodeException(String message, Long code) {
        super(message, code);
    }
}
