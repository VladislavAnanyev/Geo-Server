package com.example.mywebquizengine.common.exception;

public class CodeExpiredException extends GlobalException {
    public CodeExpiredException(String message, Long code) {
        super(message, code);
    }
}
