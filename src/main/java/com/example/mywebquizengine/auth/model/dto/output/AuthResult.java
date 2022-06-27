package com.example.mywebquizengine.auth.model.dto.output;

public class AuthResult {
    private Long userId;
    private String jwtToken;
    private String exchangeName;

    public AuthResult(Long userId, String jwtToken, String exchangeName) {
        this.userId = userId;
        this.jwtToken = jwtToken;
        this.exchangeName = exchangeName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

