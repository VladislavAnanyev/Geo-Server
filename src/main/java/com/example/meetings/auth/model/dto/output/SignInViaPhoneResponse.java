package com.example.meetings.auth.model.dto.output;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.*;
import lombok.experimental.Accessors;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class SignInViaPhoneResponse extends SuccessfulResponse {
    private AuthPhoneResult result;
}
