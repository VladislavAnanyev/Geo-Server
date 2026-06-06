package com.example.meetings.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class ApiError {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public Long getCode() {
        return code;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
