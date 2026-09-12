package com.example.meetings.geolocation.sharing.model.dto.output;

import com.example.meetings.user.model.dto.UserCommonView;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;

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
    @Value("#{target.target}")
    @JsonProperty("target")
    UserCommonView getTargetUser();

    /** @return текущий статус обмена (строка) */
    String getStatus();

    /** @return время создания приглашения */
    LocalDateTime getCreatedAt();

    /** @return время принятия приглашения, если принято */
    LocalDateTime getAcceptedAt();
}
