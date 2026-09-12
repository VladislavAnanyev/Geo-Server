package com.example.meetings.common.exception;

public class SubscriptionRequiredException extends RuntimeException {

    private final Long code;

    public SubscriptionRequiredException(String messageKey, Long code) {
        super(messageKey);
        this.code = code;
    }

    public Long getCode() {
        return code;
    }
}
