package com.example.meetings.photo.facade;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.common.service.FileStorageService;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.photo.model.dto.PhotoDto;
import com.example.meetings.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;

import static org.apache.http.entity.ContentType.IMAGE_JPEG;

@Service
public class PhotoFacadeImpl implements PhotoFacade {

    private final PhotoService photoService;
    private final FileStorageService fileStorageService;

    public PhotoFacadeImpl(PhotoService photoService, @Qualifier("s3Service") FileStorageService fileStorageService) {
        this.photoService = photoService;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public void swapPhoto(Long photoId, Integer position, Long userId) {
        photoService.swapPhoto(photoId, position, userId);
    }

    @Override
    public PhotoDto uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId) {
        if (!IMAGE_JPEG.getMimeType().equals(contentType)) {
            throw new LogicException("exception.wrong.content-type");
        }

        String fileName = fileStorageService.store(inputStream, originalFilename, contentType);
        Photo photo = photoService.savePhoto(fileName, userId);
        return new PhotoDto()
                .setUrl(photo.getUrl())
                .setPhotoId(photo.getPhotoId())
                .setPosition(photo.getPosition());
    }

    @Override
    public void deletePhoto(Long photoId, Long userId) {
        photoService.deletePhoto(photoId, userId);
    }
}
