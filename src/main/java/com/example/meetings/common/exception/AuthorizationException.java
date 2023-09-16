package com.example.meetings.common.exception;

public class AuthorizationException extends GlobalException {
    public AuthorizationException(String message, Long code) {
        super(message, code);
    }

    public AuthorizationException(){
        super("Entity Not Found", GlobalErrorCode.ERROR_WRONG_USERNAME_OR_PASSWORD);
    }

}
