package com.example.meetings.auth.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;

public class AuthResponse extends SuccessfulResponse {
    private AuthResult result;

    public AuthResponse(AuthResult result) {
        this.result = result;
    }

    public AuthResult getResult() {
        return result;
    }

    public void setResult(AuthResult result) {
        this.result = result;
    }
}
