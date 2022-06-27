package com.example.mywebquizengine.auth.model.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class VerifyCodeRequest {

    @NotNull
    @NotBlank
    String username;

    @NotNull
    @NotBlank
    String code;

    public String getUsername() {
        return username;
    }

    public String getCode() {
        return code;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
