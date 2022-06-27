package com.example.mywebquizengine.auth.model.dto.output;

import com.example.mywebquizengine.common.common.SuccessfulResponse;

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
