package com.example.mywebquizengine.meeting.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class GeolocationDto {

    /**
     * Широта
     */
    private Double lat;

    /**
     * Долгота
     */
    private Double lng;

    /**
     * Время получения информации о геолокации
     */
    private LocalDateTime lastUpdateAt;

    /**
     * Идентификатор пользователя
     */
    private Long userId;

    /**
     * Имя пользователя
     */
    private String firstName;

    /**
     * Фамилия пользователя
     */
    private String lastName;

    /**
     * Никнейм пользователя
     */
    private String username;
}
