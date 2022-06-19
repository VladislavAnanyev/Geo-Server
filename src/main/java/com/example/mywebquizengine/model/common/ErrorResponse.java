package com.example.mywebquizengine.model.common;

import com.example.mywebquizengine.model.exception.ApiError;

public class ErrorResponse implements Response {
    private ApiError error;

    public ErrorResponse(String description, String message) {
        this.error = new ApiError(message);
    }

    public ErrorResponse() {}

    public void setError(ApiError error) {
        this.error = error;
    }

    public ApiError getError() {
        return error;
    }

    @Override
    public ResponseStatus getStatus() {
        return ResponseStatus.FAIL;
    }
}
