package com.example.mywebquizengine.model.userinfo;

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
