package com.example.meetings.user.favoriteplace.repository;

import com.example.meetings.user.favoriteplace.model.domain.UserFavoritePlace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserFavoritePlaceRepository extends JpaRepository<UserFavoritePlace, Long> {

    List<UserFavoritePlace> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    boolean existsByUser_UserIdAndLatAndLng(Long userId, Double lat, Double lng);

    long countByUser_UserId(Long userId);

    Optional<UserFavoritePlace> findByIdAndUser_UserId(Long id, Long userId);
}
