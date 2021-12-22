package com.example.mywebquizengine.model.userinfo;

public class AuthResponse {
    private String jwtToken;
    private String queueName;

    public AuthResponse(String jwt) {
        this.jwtToken = jwt;
    }

    public AuthResponse(String jwt, String queueName) {
        this.jwtToken = jwt;
        this.queueName = queueName;
    }
    // геттер и сеттер


    public String getQueueName() {
        return queueName;
    }

    public void setQueueName(String queueName) {
        this.queueName = queueName;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }
}
