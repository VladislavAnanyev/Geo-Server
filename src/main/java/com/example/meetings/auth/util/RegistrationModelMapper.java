package com.example.meetings.auth.util;

import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.model.dto.input.RegistrationRequest;

public class RegistrationModelMapper {
    public static RegistrationModel map(RegistrationRequest request) {
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setUsername(request.getUsername());
        registrationModel.setFirstName(request.getFirstName());
        registrationModel.setLastName(request.getLastName());
        registrationModel.setPassword(request.getPassword());

        return registrationModel;
    }
}
