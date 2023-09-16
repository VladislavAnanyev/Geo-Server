package com.example.meetings.user.model;

import com.example.meetings.auth.model.dto.output.AuthPhoneResult;
import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAuthCodeResponse extends SuccessfulResponse {
    private AuthPhoneResult result;
}
