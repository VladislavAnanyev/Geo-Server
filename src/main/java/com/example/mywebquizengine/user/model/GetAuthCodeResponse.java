package com.example.mywebquizengine.user.model;

import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResult;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAuthCodeResponse extends SuccessfulResponse {
    private AuthPhoneResult result;
}
