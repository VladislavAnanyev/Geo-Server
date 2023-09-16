package com.example.meetings.photo.facade;

import com.example.meetings.photo.model.dto.UploadPhotoResult;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public interface PhotoFacade {
    void swapPhoto(Long photoId, Integer position, Long userId);

    UploadPhotoResult uploadPhoto(InputStream inputStream, String originalFilename, String contentType, Long userId);

    void deletePhoto(Long id, Long userId);
}
