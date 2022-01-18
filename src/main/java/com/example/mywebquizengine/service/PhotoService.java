package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.model.userinfo.Photo;
import com.example.mywebquizengine.repos.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Value("${hostname}")
    private String hostname;

    @Transactional
    public void swapPhoto(Long firstPhotoId, Long secondPhotoId, String name) throws IllegalAccessError, EntityNotFoundException {

        Optional<Photo> optionalFirstPhoto = photoRepository.findById(firstPhotoId);
        Optional<Photo> optionalSecondPhoto = photoRepository.findById(secondPhotoId);
        if (optionalFirstPhoto.isPresent() && optionalSecondPhoto.isPresent()) {
            Photo firstPhoto = optionalFirstPhoto.get();
            Photo secondPhoto = optionalSecondPhoto.get();
            if (firstPhoto.getUser().getUsername().equals(name) && secondPhoto.getUser().getUsername().equals(name)) {
                String tempPhotoUrl = firstPhoto.getUrl();
                firstPhoto.setUrl(secondPhoto.getUrl());
                secondPhoto.setUrl(tempPhotoUrl);
            } else throw new SecurityException("You are not loader of this photo");
        } else throw new EntityNotFoundException("Photo with given photoId not found");



        /*        Photo savedPhoto = optionalPhoto.get();
                List<Photo> photos = photoRepository.findByUser_Username(name);
        for (Photo photo : photos) {
            if (photo.getId().equals(firstPhotoId)) {
                photos.remove(photo);
            }*/
        /*}

        photos.indexOf()

                photos.remove(((int) savedPhoto.getPosition()));
                photos.add(photo.getPosition(), savedPhoto);

                for (int i = 0; i < photos.size(); i++) {
                    photos.get(i).setPosition(i);
                }*/

    }

    @Transactional
    public String uploadPhoto(MultipartFile file, String username) throws LogicException, IOException {
        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0, 8);
                byte[] bytes = file.getBytes();

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                String photoUrl = "https://" + hostname + "/img/" + uuid + ".jpg";

                User user = userService.loadUserByUsername(username);

                Photo photo = new Photo();
                photo.setUrl(photoUrl);
                user.addPhoto(photo);

                return photoUrl;

            } catch (IOException e) {
                throw new IOException("Input-Output error");
            }
        } else throw new LogicException("File is empty");
    }

    @Transactional
    public void deletePhoto(Long photoId, String authUsername) throws LogicException {
        Optional<Photo> optionalPhoto = photoRepository.findById(photoId);
        if (optionalPhoto.isPresent()) {
            Photo photo = optionalPhoto.get();
            if (photo.getUser().getUsername().equals(authUsername)) {
                if (photoRepository.getPhotoCountByUsername(authUsername) > 1) {
                    photoRepository.deleteById(photoId);
                } else throw new LogicException("You must have at least one photo");
            } else throw new SecurityException("You are not loader of this photo");
        } else throw new EntityNotFoundException("Photo with given photoId not found");
    }
}
