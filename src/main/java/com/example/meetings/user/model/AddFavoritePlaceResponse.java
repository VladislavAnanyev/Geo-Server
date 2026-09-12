package com.example.meetings.user.model;

import com.example.meetings.common.model.SuccessfulResponse;
import com.example.meetings.user.favoriteplace.model.dto.FavoritePlaceDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AddFavoritePlaceResponse extends SuccessfulResponse {

    private FavoritePlaceDto result;
}
