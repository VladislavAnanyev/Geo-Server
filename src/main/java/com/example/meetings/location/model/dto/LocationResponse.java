package com.example.meetings.location.model.dto;

import com.example.meetings.common.model.SuccessfulResponse;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Accessors (chain = true)
public class LocationResponse extends SuccessfulResponse {
    private List<LocationDTO> locations;
}
