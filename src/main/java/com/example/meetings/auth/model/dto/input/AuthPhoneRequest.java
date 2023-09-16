package com.example.meetings.auth.model.dto.input;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class AuthPhoneRequest {

    @NotBlank(message = "Отсутствует номер телефона")
    @Pattern(regexp = "^(\\+\\d{1,4}[(][0-9]*[)][0-9]*)",
            message = "Не соответствует требуемому паттерну")
    private String phone;
    private String firstName;
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
