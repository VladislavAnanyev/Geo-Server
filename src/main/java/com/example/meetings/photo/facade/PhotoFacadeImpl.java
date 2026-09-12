package com.example.meetings.photo.facade;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.common.service.FileStorageService;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.photo.model.dto.PhotoDto;
import com.example.meetings.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static com.example.meetings.util.Constants.ALLOWED_PHOTO_CONTENT_TYPES;

/**
 * Реализация фасада фотографий профиля.
 */
@Service
public class PhotoFacadeImpl implements PhotoFacade {

    private final PhotoService photoService;
    private final FileStorageService fileStorageService;

    public PhotoFacadeImpl(PhotoService photoService, @Qualifier("s3Service") FileStorageService fileStorageService) {
        this.photoService = photoService;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Меняет позицию фотографии в профиле пользователя.
     *
     * @param photoId  идентификатор фотографии
     * @param position новая позиция в галерее
     * @param userId   идентификатор владельца фотографии
     */
    @Override
    public void swapPhoto(Long photoId, Integer position, Long userId) {
        photoService.swapPhoto(photoId, position, userId);
    }

    /**
     * Загружает фотографию в хранилище и сохраняет запись в профиле пользователя.
     *
     * @param inputStream      поток данных загружаемого файла
     * @param originalFilename исходное имя файла
     * @param contentType      MIME-тип файла
     * @param userId           идентификатор владельца фотографии
     * @return данные сохранённой фотографии
     */
    @Override
    public PhotoDto uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId) {
        if (contentType == null || !ALLOWED_PHOTO_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new LogicException("exception.wrong.content-type");
        }

        String fileName = fileStorageService.store(inputStream, originalFilename, contentType);
        Photo photo = photoService.savePhoto(fileName, userId);
        return new PhotoDto()
                .setUrl(photo.getUrl())
                .setPhotoId(photo.getPhotoId())
                .setPosition(photo.getPosition());
    }

    /**
     * Удаляет фотографию из профиля пользователя.
     *
     * @param photoId идентификатор фотографии
     * @param userId  идентификатор владельца фотографии
     */
    @Override
    public void deletePhoto(Long photoId, Long userId) {
        photoService.deletePhoto(photoId, userId);
    }
}
