package com.example.meetings.auth.model.dto.input;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class RegistrationRequest {
    @NotNull
    @NotBlank
    private String username;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    private String lastName;

    @NotNull
    @NotBlank
    @Size(min = 5)
    private String password;
}
