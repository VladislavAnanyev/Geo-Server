package com.example.mywebquizengine.auth.model.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class GoogleToken {
    @NotNull
    @NotBlank
    private String idTokenString;

    public String getIdTokenString() {
        return idTokenString;
    }

    public void setIdTokenString(String idTokenString) {
        this.idTokenString = idTokenString;
    }

}
