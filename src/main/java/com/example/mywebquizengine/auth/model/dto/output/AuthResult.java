package com.example.mywebquizengine.auth.model.dto.output;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AuthResult {
    private Long userId;
    private String jwtToken;
    private String refreshToken;
    private String exchangeName;
}

