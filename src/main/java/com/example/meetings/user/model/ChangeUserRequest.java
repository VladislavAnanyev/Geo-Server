package com.example.meetings.user.model;

import com.example.meetings.user.model.domain.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ChangeUserRequest {
    private String firstName;
    private String description;
    private Gender gender;
    private LocalDate birthDate;
}
