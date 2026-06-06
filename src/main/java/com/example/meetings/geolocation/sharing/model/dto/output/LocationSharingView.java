package com.example.meetings.geolocation.sharing.model.dto.output;

import com.example.meetings.user.model.dto.UserCommonView;

import java.time.LocalDateTime;

/**
 * Проекция для ответов API по записям обмена геолокацией.
 */
public interface LocationSharingView {

    /** @return идентификатор записи обмена */
    Long getSharingId();

    /** @return пользователь, отправивший приглашение */
    UserCommonView getInitiator();

    /** @return пользователь, получивший приглашение */
    UserCommonView getTarget();

    /** @return текущий статус обмена (строка) */
    String getStatus();

    /** @return время создания приглашения */
    LocalDateTime getCreatedAt();

    /** @return время принятия приглашения, если принято */
    LocalDateTime getAcceptedAt();
}
