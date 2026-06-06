package com.example.meetings.geolocation.sharing.repository;

import com.example.meetings.geolocation.sharing.model.domain.LocationSharing;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus;
import com.example.meetings.geolocation.sharing.model.dto.output.LocationSharingView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий записей согласия на обмен геолокацией.
 */
public interface LocationSharingRepository extends JpaRepository<LocationSharing, Long> {

    /**
     * Проверяет наличие записи с заданным направлением (инициатор → получатель) и статусом.
     */
    boolean existsByStatusAndInitiator_UserIdAndTarget_UserId(
            LocationSharingStatus status,
            Long initiatorUserId,
            Long targetUserId
    );

    /**
     * Возвращает идентификаторы партнёров с активным обменом (в обоих направлениях).
     */
    @Query("""
            SELECT CASE WHEN ls.initiator.userId = :userId THEN ls.target.userId ELSE ls.initiator.userId END
            FROM LOCATION_SHARING ls
            WHERE ls.status = com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.ACTIVE
              AND (ls.initiator.userId = :userId OR ls.target.userId = :userId)
            """)
    List<Long> findActivePartnerIds(@Param("userId") Long userId);

    /**
     * Находит активную запись обмена между двумя пользователями (независимо от направления).
     */
    @Query("""
            SELECT ls FROM LOCATION_SHARING ls
            WHERE ls.status = com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.ACTIVE
              AND ((ls.initiator.userId = :userId AND ls.target.userId = :friendUserId)
                OR (ls.initiator.userId = :friendUserId AND ls.target.userId = :userId))
            """)
    Optional<LocationSharing> findActiveBetween(@Param("userId") Long userId, @Param("friendUserId") Long friendUserId);

    /**
     * Находит запись по паре инициатор → получатель.
     */
    Optional<LocationSharing> findByInitiator_UserIdAndTarget_UserId(Long initiatorUserId, Long targetUserId);

    /**
     * Находит запись по паре инициатор → получатель и статусу.
     */
    Optional<LocationSharing> findByInitiator_UserIdAndTarget_UserIdAndStatus(
            Long initiatorUserId,
            Long targetUserId,
            LocationSharingStatus status
    );

    /**
     * Все входящие приглашения в заданном статусе для получателя.
     */
    List<LocationSharingView> findAllByTarget_UserIdAndStatus(Long targetUserId, LocationSharingStatus status);

    /**
     * Входящие приглашения от конкретного отправителя к получателю.
     */
    List<LocationSharingView> findAllByTarget_UserIdAndInitiator_UserIdAndStatus(
            Long targetUserId,
            Long initiatorUserId,
            LocationSharingStatus status
    );

    /**
     * Все исходящие приглашения в заданном статусе от инициатора.
     */
    List<LocationSharingView> findAllByInitiator_UserIdAndStatus(Long initiatorUserId, LocationSharingStatus status);

    /**
     * Исходящее приглашение для пары инициатор → получатель.
     */
    List<LocationSharingView> findAllByInitiator_UserIdAndTarget_UserIdAndStatus(
            Long initiatorUserId,
            Long targetUserId,
            LocationSharingStatus status
    );

    /**
     * Все активные записи, где участвует указанный пользователь.
     */
    @Query("""
            SELECT ls FROM LOCATION_SHARING ls
            WHERE ls.status = com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.ACTIVE
              AND (ls.initiator.userId = :userId OR ls.target.userId = :userId)
            """)
    List<LocationSharing> findAllActiveForUser(@Param("userId") Long userId);

    /**
     * Переводит все записи между двумя пользователями в статус REVOKED (в обоих направлениях).
     */
    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE LOCATION_SHARING ls SET ls.status = com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.REVOKED
            WHERE (ls.initiator.userId = :userId AND ls.target.userId = :friendUserId)
               OR (ls.initiator.userId = :friendUserId AND ls.target.userId = :userId)
            """)
    void revokeAllBetween(@Param("userId") Long userId, @Param("friendUserId") Long friendUserId);
}
