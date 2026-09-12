package com.example.meetings.user.model.dto;

import com.example.meetings.user.favoriteplace.model.dto.FavoritePlaceDto;
import com.example.meetings.user.model.domain.Gender;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDate;
import java.util.List;

@Data
@Accessors(chain = true)
public class UserProfileDto {

    private Long userId;
    private String username;
    private String firstName;
    private String description;
    private Gender gender;
    private LocalDate birthDate;
    private List<PhotoView> photos;
    private String online;
    private List<FavoritePlaceDto> favoritePlaces;

    public static UserProfileDto from(ProfileView profile, List<FavoritePlaceDto> favoritePlaces) {
        return new UserProfileDto()
                .setUserId(profile.getUserId())
                .setUsername(profile.getUsername())
                .setFirstName(profile.getFirstName())
                .setDescription(profile.getDescription())
                .setGender(profile.getGender())
                .setBirthDate(profile.getBirthDate())
                .setPhotos(profile.getPhotos())
                .setOnline(profile.getOnline())
                .setFavoritePlaces(favoritePlaces);
    }
}
