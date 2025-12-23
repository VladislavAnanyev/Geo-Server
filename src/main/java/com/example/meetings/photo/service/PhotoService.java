package com.example.meetings.photo.service;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.photo.repository.PhotoRepository;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import com.example.meetings.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Сервис для работы с изображениями пользователей
 */
@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Value("${hostname}")
    private String hostname;

    /**
     * Получить фотографию пользователя по ее идентификатору
     *
     * @param id идентификатор фотографии
     * @return информация о фотографии
     */
    public Photo findPhotoById(Long id) {
        Optional<Photo> optionalPhoto = photoRepository.findById(id);
        if (optionalPhoto.isEmpty()) {
            throw new EntityNotFoundException("Photo with given photoId not found");
        }

        return optionalPhoto.get();
    }

    /**
     * Поставить фотографию пользователя на указанную позицию в списке его фотографий
     *
     * @param photoId  идентификатор фотографии
     * @param position позиция
     * @param userId   идентификатор пользователя
     */
    @Transactional
    public void swapPhoto(Long photoId, Integer position, Long userId) {
        Photo photo = findPhotoById(photoId);

        if (!photo.getUser().getUserId().equals(userId)) {
            throw new SecurityException("You are not loader of this photo");
        }

        List<Photo> photos = photoRepository.findPhotosByUserId(userId);

        if (position == 0) {
            photo.getUser().setMainPhoto(
                    new Photo()
                            .setUrl(photo.getUrl())
                            .setPosition(0)
                            .setUser(userRepository.getById(userId))
            );
        }

        photos.remove(((int) photo.getPosition()));
        photos.add(position, photo);

        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
    }

    /**
     * Сохранить фотографию пользователя
     *
     * @param url    ресурс по которому доступен файл
     * @param userId идентификатор пользователя
     * @return URI фотографии
     */
    @Transactional
    public Photo savePhoto(String url, Long userId) {
        User user = userService.loadUserByUserId(userId);
        Photo photo = new Photo()
                .setUrl(url)
                .setUser(user)
                .setPosition(user.getPhotos().size());

        user.addPhoto(photo);

        return photo;
    }

    /**
     * Удалить фотографию
     *
     * @param photoId    идентификатор фотографии
     * @param authUserId идентификатор пользователя
     */
    public void deletePhoto(Long photoId, Long authUserId) {
        Photo photo = findPhotoById(photoId);
        if (!photo.getUser().getUserId().equals(authUserId)) {
            throw new SecurityException("You are not loader of this photo");
        }

        if (photoRepository.getPhotoCountByUserId(authUserId) <= 1) {
            throw new LogicException("exception.photo.must.exist");
        }

        List<Photo> photos = photoRepository.findPhotosByUserId(authUserId);

        // если удаляется фотография с первого места, то аватаром пользователя становится его вторая фотография
        if (photo.getPosition() == 0) {
            User user = photo.getUser();
            user.setMainPhoto(photos.get(1));
            userRepository.save(user);
        }

        photoRepository.deleteById(photoId);
        photos.remove(photo);

        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }
        photoRepository.saveAll(photos);
    }
}
