package com.example.meetings.geolocation.sharing.model.domain;

import com.example.meetings.chat.model.domain.Notifiable;
import com.example.meetings.user.model.domain.User;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Согласие на обмен геолокацией между двумя друзьями.
 * Одна запись — одно направленное приглашение от инициатора к получателю.
 */
@Entity(name = "LOCATION_SHARING")
@Getter
@Setter
@Accessors(chain = true)
public class LocationSharing implements Notifiable {

    /**
     * Идентификатор записи обмена геолокацией.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "sharing_id")
    private Long sharingId;

    /**
     * Пользователь, отправивший приглашение на обмен геолокацией.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_user_id", nullable = false)
    private User initiator;

    /**
     * Пользователь, который может принять или отклонить приглашение.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_user_id", nullable = false)
    private User target;

    /**
     * Текущий статус согласия на обмен геолокацией.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private LocationSharingStatus status;

    /**
     * Время создания приглашения.
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Время принятия приглашения (когда обмен стал активным).
     */
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    /**
     * Возвращает обоих участников отношения обмена геолокацией.
     *
     * @return инициатор и получатель приглашения
     */
    public Set<User> getUsers() {
        return Set.of(initiator, target);
    }

    /**
     * Возвращает пользователей, которым нужно отправить уведомление по событию.
     * Для нового приглашения это получатель.
     *
     * @return получатели уведомления
     */
    @Override
    public Set<User> getUsersToSendNotification() {
        return Set.of(target);
    }
}
