package com.example.meetings.common.exception;

public class WrongChangePasswordCodeException extends GlobalException {
    public WrongChangePasswordCodeException(String message, Long code) {
        super(message, code);
    }
}
