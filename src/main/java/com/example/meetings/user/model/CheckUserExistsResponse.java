package com.example.meetings.user.model;

import com.example.meetings.auth.model.dto.output.UserExistDto;
import com.example.meetings.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckUserExistsResponse extends SuccessfulResponse {
    private UserExistDto result;
}
