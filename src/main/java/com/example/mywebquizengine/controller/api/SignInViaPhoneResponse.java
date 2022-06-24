package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.common.SuccessfulResponse;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthPhoneResponse;

public class SignInViaPhoneResponse extends SuccessfulResponse {

    private AuthPhoneResponse result;

    public AuthPhoneResponse getResult() {
        return result;
    }

    public void setResult(AuthPhoneResponse result) {
        this.result = result;
    }

    public SignInViaPhoneResponse(AuthPhoneResponse authPhoneResponse) {
        this.result = authPhoneResponse;
    }
}
