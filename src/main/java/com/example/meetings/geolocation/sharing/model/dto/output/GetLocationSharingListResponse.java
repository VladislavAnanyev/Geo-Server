package com.example.meetings.geolocation.sharing.model.dto.output;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Ответ API со списком записей обмена геолокацией.
 */
@Data
@Accessors(chain = true)
public class GetLocationSharingListResponse {

    /**
     * Записи обмена для запрошенного списка (входящие, исходящие или активные).
     */
    private List<LocationSharingView> items;
}
