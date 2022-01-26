package com.example.mywebquizengine.model.userinfo;

public class AuthResponse {
    private String jwtToken;
    private String exchangeName;

    public AuthResponse(String jwtToken, String exchangeName) {
        this.jwtToken = jwtToken;
        this.exchangeName = exchangeName;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }
    // геттер и сеттер

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
