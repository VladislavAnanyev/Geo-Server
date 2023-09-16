package com.example.meetings.photo.facade;

import com.example.meetings.common.service.FileSystemStorageService;
import com.example.meetings.photo.model.dto.UploadPhotoResult;
import com.example.meetings.photo.service.PhotoService;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class PhotoFacadeImpl implements PhotoFacade {

    private final PhotoService photoService;
    private final FileSystemStorageService fileSystemStorageService;

    public PhotoFacadeImpl(PhotoService photoService, FileSystemStorageService fileSystemStorageService) {
        this.photoService = photoService;
        this.fileSystemStorageService = fileSystemStorageService;
    }

    @Override
    public void swapPhoto(Long photoId, Integer position, Long userId) {
        photoService.swapPhoto(photoId, position, userId);
    }

    @Override
    public UploadPhotoResult uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId) {
        String fileName = fileSystemStorageService.store(contentType, originalFilename, inputStream);
        return new UploadPhotoResult()
                .setUri(photoService.savePhoto(fileName, userId));
    }

    @Override
    public void deletePhoto(Long photoId, Long userId) {
        photoService.deletePhoto(photoId, userId);
    }
}
