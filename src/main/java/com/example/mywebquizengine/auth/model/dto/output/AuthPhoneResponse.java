package com.example.mywebquizengine.auth.model.dto.output;

public class AuthPhoneResponse {
    private String code;

    public AuthPhoneResponse() {}

    public AuthPhoneResponse(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
