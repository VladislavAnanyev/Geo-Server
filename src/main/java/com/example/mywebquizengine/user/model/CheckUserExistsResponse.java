package com.example.mywebquizengine.user.model;

import com.example.mywebquizengine.auth.model.dto.output.UserExistDto;
import com.example.mywebquizengine.common.model.SuccessfulResponse;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CheckUserExistsResponse extends SuccessfulResponse {
    private UserExistDto result;
}
