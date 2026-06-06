package com.example.meetings.geolocation.sharing.model.dto.input;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Тело запроса для операций, где текущий пользователь указывает друга.
 */
@Data
@Accessors(chain = true)
public class LocationSharingFriendRequest {

    /**
     * Идентификатор друга (приглашение, отзыв, фильтр списков).
     */
    @NotNull
    private Long friendUserId;
}
