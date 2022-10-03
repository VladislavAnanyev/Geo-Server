package com.example.mywebquizengine.controller.api;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RefreshTokenRequest {
    private String refreshToken;
}
