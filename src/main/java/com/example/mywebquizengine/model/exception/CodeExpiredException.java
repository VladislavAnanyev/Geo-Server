package com.example.mywebquizengine.model.exception;

import com.example.mywebquizengine.model.common.ErrorResponse;
import com.example.mywebquizengine.model.exception.GlobalException;

public class CodeExpiredException extends GlobalException {
    public CodeExpiredException(String message, Long code) {
        super(message, code);
    }
}
