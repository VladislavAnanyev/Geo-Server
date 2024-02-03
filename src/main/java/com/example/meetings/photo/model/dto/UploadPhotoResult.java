package com.example.meetings.photo.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UploadPhotoResult {
    /**
     * URI по которой доступна фотография
     */
    private String uri;
    /**
     * Идентификатор
     */
    private Long id;
    /**
     * Текущая позиция
     */
    private Integer position;
}
