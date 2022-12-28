package com.example.mywebquizengine.user.model;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.user.model.dto.AuthUserView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetAuthUserResponse extends SuccessfulResponse {
    private AuthUserView result;
}
