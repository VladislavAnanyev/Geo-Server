package com.example.mywebquizengine.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ApiError {
    private String message;
    private String debugMessage;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError() {}

    public ApiError(String message, String debugMessage){
        this.message=message;
        this.debugMessage=debugMessage;
    }

    public ApiError(String message, String debugMessage, List<String> errors) {
        this.message=message;
        this.debugMessage=debugMessage;
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getDebugMessage() {
        return debugMessage;
    }

    public String getMessage() {
        return message;
    }

    public void setDebugMessage(String debugMessage) {
        this.debugMessage = debugMessage;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
