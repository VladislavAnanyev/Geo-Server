package com.example.meetings.request.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GetRequestsToUserResult {
    private List<RequestView> requests;
}
