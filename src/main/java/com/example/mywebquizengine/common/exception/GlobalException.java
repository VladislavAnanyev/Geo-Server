package com.example.mywebquizengine.common.exception;

public class GlobalException extends RuntimeException {
    private Long code;

    public GlobalException(String message, Long code) {
        super(message);
        this.code = code;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }
}
