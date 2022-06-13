package com.example.mywebquizengine.model.userinfo;

public class AuthResult {
    private String jwtToken;
    private String exchangeName;

    public AuthResult(String jwtToken, String exchangeName) {
        this.jwtToken = jwtToken;
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}

