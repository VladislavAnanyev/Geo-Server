package com.example.mywebquizengine.user.model;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.example.mywebquizengine.user.model.dto.ProfileView;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserProfileResponse extends SuccessfulResponse {
    private ProfileView result;
}
