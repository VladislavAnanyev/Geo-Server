package com.example.meetings.integration.sigmasms.dto;

import lombok.Getter;

@Getter
public enum SigmaSmsMessageType {
    VOICE("voice"),
    SMS("sms"),
    TELEGRAM("telegramcode");

    private final String value;

    SigmaSmsMessageType(String value) {
        this.value = value;
    }
}