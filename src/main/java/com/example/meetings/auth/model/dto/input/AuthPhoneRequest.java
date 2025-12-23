package com.example.meetings.auth.model.dto.input;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AuthPhoneRequest {
    @NotNull
    @NotBlank(message = "Отсутствует номер телефона")
    private String phone;
}
