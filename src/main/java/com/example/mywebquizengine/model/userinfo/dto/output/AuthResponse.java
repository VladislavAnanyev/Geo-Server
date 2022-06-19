package com.example.mywebquizengine.model.userinfo.dto.output;

import com.example.mywebquizengine.model.common.SuccessfulResponse;

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
