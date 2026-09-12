package com.example.meetings.user.model;

import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.model.dto.UserProfileDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetUserProfileResponse extends SuccessfulResponse {
    private UserProfileDto result;
}
