package com.example.meetings.auth.model;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RefreshTokenRequest {
    private String refreshToken;
}
