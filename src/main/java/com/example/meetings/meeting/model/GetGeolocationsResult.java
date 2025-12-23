package com.example.meetings.meeting.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class GetGeolocationsResult {
    /**
     * Список информации о геолокации
     */
    private List<GeolocationDto> items;
}
