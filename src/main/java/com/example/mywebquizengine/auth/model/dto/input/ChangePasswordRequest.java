package com.example.mywebquizengine.auth.model.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ChangePasswordRequest {

    @NotNull
    @NotBlank
    private String username;
    @NotNull
    @NotBlank
    private String code;
    @NotNull
    @NotBlank
    private String password;

    public String getPassword() {
        return password;
    }

    public String getCode() {
        return code;
    }

    public String getUsername() {
        return username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
