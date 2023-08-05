package com.example.mywebquizengine.meeting.model;

import com.example.mywebquizengine.common.model.SuccessfulResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetGeolocationsResponse extends SuccessfulResponse {
    @JsonProperty("result")
    private GetGeolocationsResult result;
}
