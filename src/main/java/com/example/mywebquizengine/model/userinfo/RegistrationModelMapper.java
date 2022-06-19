package com.example.mywebquizengine.model.userinfo;

import com.example.mywebquizengine.model.userinfo.dto.input.RegistrationRequest;

public class RegistrationModelMapper {
    public static RegistrationModel map(RegistrationRequest request) {
        RegistrationModel registrationModel = new RegistrationModel();
        registrationModel.setUsername(request.getUsername());
        registrationModel.setEmail(request.getEmail());
        registrationModel.setFirstName(request.getFirstName());
        registrationModel.setLastName(request.getLastName());
        registrationModel.setPassword(request.getPassword());
        return registrationModel;
    }
}
