package com.example.meetings.photo.facade;

import com.example.meetings.common.service.FileStorageService;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.photo.model.dto.UploadPhotoResult;
import com.example.meetings.photo.service.PhotoService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;

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
    public UploadPhotoResult uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId) {
        String fileName = fileStorageService.store(inputStream, originalFilename, contentType);
        Photo photo = photoService.savePhoto(fileName, userId);
        return new UploadPhotoResult()
                .setUri(photo.getUrl())
                .setId(photo.getPhotoId())
                .setPosition(photo.getPosition());
    }

    @Override
    public void deletePhoto(Long photoId, Long userId) {
        photoService.deletePhoto(photoId, userId);
    }
}
