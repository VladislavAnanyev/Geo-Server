package com.example.meetings.photo.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data //todo объединить с PhotoView
@Accessors(chain = true)
public class PhotoDto {
    /**
     * URI по которой доступна фотография
     */
    private String url;
    /**
     * Идентификатор
     */
    private Long photoId;
    /**
     * Текущая позиция
     */
    private Integer position;
}
