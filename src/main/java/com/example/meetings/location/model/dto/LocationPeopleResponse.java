package com.example.meetings.location.model.dto;

import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.model.dto.UserCommonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors(chain = true)
public class LocationPeopleResponse extends SuccessfulResponse {
    private List<UserCommonView> people;
}
