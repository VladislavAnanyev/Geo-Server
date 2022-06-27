package com.example.mywebquizengine.photo.repository;

import com.example.mywebquizengine.photo.model.domain.Photo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PhotoRepository extends CrudRepository<Photo, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM USERS_PHOTOS WHERE USER_ID =:userId ORDER BY POSITION")
    List<Photo> findByUser_UserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM USERS_PHOTOS WHERE USER_ID = :userId")
    Integer getPhotoCountByUserId(Long userId);

}
