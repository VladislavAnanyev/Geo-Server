package com.example.meetings.user.model;

import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.model.dto.AuthUserView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAuthUserResponse extends SuccessfulResponse {
    private AuthUserView result;
}
