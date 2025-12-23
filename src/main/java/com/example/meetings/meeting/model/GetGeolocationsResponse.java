package com.example.meetings.meeting.model;

import com.example.meetings.common.model.SuccessfulResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGeolocationsResponse extends SuccessfulResponse {
    @JsonProperty("result")
    private GetGeolocationsResult result;
}
