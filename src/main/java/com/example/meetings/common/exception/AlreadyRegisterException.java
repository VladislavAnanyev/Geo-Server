package com.example.meetings.common.exception;

public class AlreadyRegisterException extends GlobalException {
    public AlreadyRegisterException(String message, Long code) {
        super(message, code);
    }
}
