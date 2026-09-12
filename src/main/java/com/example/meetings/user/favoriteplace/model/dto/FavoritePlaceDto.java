package com.example.meetings.user.favoriteplace.model.dto;

import com.example.meetings.user.favoriteplace.model.domain.UserFavoritePlace;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class FavoritePlaceDto {

    private Long id;
    private String name;
    private Double lat;
    private Double lng;
    private LocalDateTime createdAt;

    public static FavoritePlaceDto from(UserFavoritePlace place) {
        return new FavoritePlaceDto()
                .setId(place.getId())
                .setName(place.getName())
                .setLat(place.getLat())
                .setLng(place.getLng())
                .setCreatedAt(place.getCreatedAt());
    }
}
