package com.example.meetings.common.exception;

import static com.example.meetings.util.Constants.ERROR_WRONG_USERNAME_OR_PASSWORD;

public class AuthorizationException extends GlobalException {
    public AuthorizationException(String message, Long code) {
        super(message, code);
    }

    public AuthorizationException() {
        super("exception.authorization", ERROR_WRONG_USERNAME_OR_PASSWORD);
    }

}
