package com.example.meetings.auth.model.dto.output;

import lombok.*;
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

