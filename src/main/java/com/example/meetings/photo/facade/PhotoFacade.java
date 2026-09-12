package com.example.meetings.photo.facade;

import com.example.meetings.photo.model.dto.PhotoDto;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Фасад сценариев API фотографий профиля.
 */
@Service
public interface PhotoFacade {

    /**
     * Меняет позицию фотографии в профиле пользователя.
     *
     * @param photoId  идентификатор фотографии
     * @param position новая позиция в галерее
     * @param userId   идентификатор владельца фотографии
     */
    void swapPhoto(Long photoId, Integer position, Long userId);

    /**
     * Загружает JPEG-фотографию в хранилище и сохраняет запись в профиле пользователя.
     *
     * @param inputStream      поток данных загружаемого файла
     * @param originalFilename исходное имя файла
     * @param contentType      MIME-тип файла
     * @param userId           идентификатор владельца фотографии
     * @return данные сохранённой фотографии
     */
    PhotoDto uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId);

    /**
     * Удаляет фотографию из профиля пользователя.
     *
     * @param id     идентификатор фотографии
     * @param userId идентификатор владельца фотографии
     */
    void deletePhoto(Long id, Long userId);
}
