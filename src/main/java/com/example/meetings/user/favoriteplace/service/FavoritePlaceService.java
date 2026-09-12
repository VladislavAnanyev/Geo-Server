package com.example.meetings.user.favoriteplace.service;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.user.favoriteplace.model.AddFavoritePlaceRequest;
import com.example.meetings.user.favoriteplace.model.domain.UserFavoritePlace;
import com.example.meetings.user.favoriteplace.model.dto.FavoritePlaceDto;
import com.example.meetings.user.favoriteplace.repository.UserFavoritePlaceRepository;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.meetings.util.Constants.MAX_USER_FAVORITE_PLACES;
import static java.util.stream.Collectors.toList;

@Service
@Validated
public class FavoritePlaceService {

    private final UserFavoritePlaceRepository userFavoritePlaceRepository;
    private final UserService userService;

    public FavoritePlaceService(UserFavoritePlaceRepository userFavoritePlaceRepository, UserService userService) {
        this.userFavoritePlaceRepository = userFavoritePlaceRepository;
        this.userService = userService;
    }

    @Transactional(readOnly = true)
    public List<FavoritePlaceDto> findByUserId(Long userId) {
        return userFavoritePlaceRepository.findByUser_UserIdOrderByCreatedAtDesc(userId).stream()
                .map(FavoritePlaceDto::from)
                .collect(toList());
    }

    @Transactional
    public FavoritePlaceDto add(Long userId, @Valid AddFavoritePlaceRequest request) {
        if (userFavoritePlaceRepository.countByUser_UserId(userId) >= MAX_USER_FAVORITE_PLACES) {
            throw new LogicException("exception.favorite-place.limit.exceeded");
        }
        if (userFavoritePlaceRepository.existsByUser_UserIdAndLatAndLng(userId, request.getLat(), request.getLng())) {
            throw new LogicException("exception.favorite-place.already.exists");
        }

        User user = userService.loadUserByUserIdProxy(userId);
        UserFavoritePlace place = userFavoritePlaceRepository.save(
                new UserFavoritePlace()
                        .setUser(user)
                        .setName(request.getName().trim())
                        .setLat(request.getLat())
                        .setLng(request.getLng())
                        .setCreatedAt(LocalDateTime.now())
        );
        return FavoritePlaceDto.from(place);
    }

    @Transactional
    public void remove(Long userId, Long favoritePlaceId) {
        UserFavoritePlace place = userFavoritePlaceRepository.findByIdAndUser_UserId(favoritePlaceId, userId)
                .orElseThrow(() -> new LogicException("exception.favorite-place.not.found"));
        userFavoritePlaceRepository.delete(place);
    }
}
