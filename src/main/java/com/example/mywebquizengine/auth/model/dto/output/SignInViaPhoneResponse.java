package com.example.mywebquizengine.auth.model.dto.output;

import com.example.mywebquizengine.common.common.SuccessfulResponse;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
public class SignInViaPhoneResponse extends SuccessfulResponse {
    private AuthPhoneResponse result;
}
