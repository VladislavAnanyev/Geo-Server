package com.example.mywebquizengine.auth.model.dto.output;

import com.example.mywebquizengine.common.common.SuccessfulResponse;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SignInViaPhoneResponse extends SuccessfulResponse {
    private AuthPhoneResponse result;
}
