package com.example.mywebquizengine.photo.service;

import com.example.mywebquizengine.common.exception.LogicException;
import com.example.mywebquizengine.photo.model.domain.Photo;
import com.example.mywebquizengine.photo.repository.PhotoRepository;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Value("${hostname}")
    private String hostname;

    @Transactional
    public void swapPhoto(Long photoId, Integer position, Long userId) throws IllegalAccessError, EntityNotFoundException {

        Optional<Photo> optionalPhoto = photoRepository.findById(photoId);
        if (optionalPhoto.isEmpty()) {
            throw new EntityNotFoundException("Photo with given photoId not found");
        }

        if (!optionalPhoto.get().getUser().getUserId().equals(userId)) {
            throw new SecurityException("You are not loader of this photo");
        }

        Photo savedPhoto = optionalPhoto.get();

        List<Photo> photos = photoRepository.findByUser_UserId(userId);

        if (position == 0) {
            savedPhoto.getUser().setAvatar(savedPhoto.getUrl());
        }

        photos.remove(((int) savedPhoto.getPosition()));
        photos.add(position, savedPhoto);

        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
    }

    @Transactional
    public String savePhoto(String fileName, Long userId) {
        String photoUrl = hostname + "/img/" + fileName;
        User user = userService.loadUserByUserId(userId);
        Photo photo = new Photo();
        photo.setUrl(photoUrl);
        photo.setPosition(user.getPhotos().size());
        user.addPhoto(photo);
        return photoUrl;
    }

    @Transactional
    public void deletePhoto(Long photoId, Long authUserId) throws LogicException {
        Optional<Photo> optionalPhoto = photoRepository.findById(photoId);
        if (optionalPhoto.isEmpty()) {
            throw new EntityNotFoundException("Photo with given photoId not found");
        }
        Photo photo = optionalPhoto.get();
        if (!photo.getUser().getUserId().equals(authUserId)) {
            throw new SecurityException("You are not loader of this photo");
        }

        if (photoRepository.getPhotoCountByUserId(authUserId) <= 1) {
            throw new LogicException("You must have at least one photo");
        }

        List<Photo> photos = photoRepository.findByUser_UserId(authUserId);
        if (photo.getPosition() == 0) {
            photo.getUser().setAvatar(photos.get(1).getUrl());
        }

        photoRepository.deleteById(photoId);

        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
    }
}
