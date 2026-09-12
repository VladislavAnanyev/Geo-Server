package com.example.meetings.auth.model.dto.input;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.*;

@Data
@Accessors(chain = true)
public class RegistrationModel {

    @NotNull
    @NotBlank
    private String phoneNumber;

    @NotNull
    @NotBlank
    private String firstName;

    @NotNull
    @NotBlank
    @Size(min = 5)
    private String password;

    private String avatar;
}
