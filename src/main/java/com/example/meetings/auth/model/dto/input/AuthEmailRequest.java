package com.example.meetings.auth.model.dto.input;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AuthEmailRequest {
    @NotBlank
    @Email
    private String email;
}
