package com.example.mywebquizengine.model.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

public class ApiError {

    private String description;
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> errors;

    public ApiError() {}

    public ApiError(String debugMessage, String message) {
        this.description = debugMessage;
        this.message = message;
    }

    public ApiError(String message, String debugMessage, List<String> errors) {
        this.description = debugMessage;
        this.message = message;
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
