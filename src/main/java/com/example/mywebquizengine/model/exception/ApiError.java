package com.example.mywebquizengine.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ApiError {

    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError() {}

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String message, List<String> errors) {
        this.message = message;
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
