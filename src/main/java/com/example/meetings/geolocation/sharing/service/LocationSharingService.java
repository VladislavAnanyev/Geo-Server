package com.example.meetings.geolocation.sharing.service;

import com.example.meetings.common.exception.LogicException;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharing;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus;
import com.example.meetings.geolocation.sharing.model.dto.output.LocationSharingView;
import com.example.meetings.geolocation.sharing.repository.LocationSharingRepository;
import com.example.meetings.user.service.FriendService;
import com.example.meetings.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Бизнес-логика согласия на обмен геолокацией между друзьями.
 */
@Service
@RequiredArgsConstructor
public class LocationSharingService {

    private final LocationSharingRepository locationSharingRepository;
    private final FriendService friendService;
    private final UserService userService;

    /**
     * Отправляет приглашение на обмен геолокацией от инициатора к другу.
     * При встречном ожидающем приглашении выполняет авто-принятие.
     */
    @Transactional
    public LocationSharing sendInvite(Long initiatorUserId, Long friendUserId) {
        validate(initiatorUserId, friendUserId);

        Optional<LocationSharing> mutualPending = locationSharingRepository
                .findByInitiator_UserIdAndTarget_UserIdAndStatus(friendUserId, initiatorUserId, PENDING);
        if (mutualPending.isPresent()) {
            return activate(mutualPending.get());
        }

        Optional<LocationSharing> existingOutgoing = locationSharingRepository
                .findByInitiator_UserIdAndTarget_UserId(initiatorUserId, friendUserId);
        if (existingOutgoing.isPresent()) {
            throw new LogicException("exception.location.sharing.already.pending");
        }

        return locationSharingRepository.save(new LocationSharing()
                .setInitiator(userService.loadUserByUserIdProxy(initiatorUserId))
                .setTarget(userService.loadUserByUserIdProxy(friendUserId))
                .setStatus(PENDING)
                .setCreatedAt(LocalDateTime.now()));
    }

    /**
     * Принимает ожидающее приглашение (только получатель).
     */
    @Transactional
    public LocationSharing accept(Long sharingId, Long targetUserId) {
        LocationSharing sharing = loadSharing(sharingId);
        if (!sharing.getTarget().getUserId().equals(targetUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "access denied");
        }
        if (!PENDING.equals(sharing.getStatus())) {
            throw new LogicException("exception.location.sharing.not.pending");
        }
        return activate(sharing);
    }

    /**
     * Отклоняет ожидающее приглашение (только получатель).
     */
    @Transactional
    public LocationSharing reject(Long sharingId, Long targetUserId) {
        LocationSharing sharing = loadSharing(sharingId);
        if (!sharing.getTarget().getUserId().equals(targetUserId)) {
            throw new ResponseStatusException(FORBIDDEN, "access denied");
        }
        if (!PENDING.equals(sharing.getStatus())) {
            throw new LogicException("exception.location.sharing.not.pending");
        }
        sharing.setStatus(REJECTED);
        return locationSharingRepository.save(sharing);
    }

    /**
     * Отзывает активный обмен геолокацией между двумя пользователями.
     */
    @Transactional
    public void revoke(Long userId, Long friendUserId) {
        LocationSharing sharing = locationSharingRepository.findActiveBetween(userId, friendUserId)
                .orElseThrow(() -> new LogicException("exception.location.sharing.not.active"));
        sharing.setStatus(REVOKED);
        locationSharingRepository.save(sharing);
    }

    /**
     * Отзывает все записи обмена между пользователями (в обоих направлениях).
     */
    @Transactional
    public void revokeAllBetween(Long userId, Long friendUserId) {
        locationSharingRepository.revokeAllBetween(userId, friendUserId);
    }

    /**
     * Возвращает входящие приглашения в статусе PENDING.
     *
     * @param fromUserId опциональный фильтр по идентификатору отправителя
     */
    @Transactional(readOnly = true)
    public List<LocationSharingView> getIncomingPending(Long userId, Long fromUserId) {
        if (fromUserId != null) {
            return locationSharingRepository.findAllByTarget_UserIdAndInitiator_UserIdAndStatus(userId, fromUserId, PENDING);
        }
        return locationSharingRepository.findAllByTarget_UserIdAndStatus(userId, PENDING);
    }

    /**
     * Возвращает исходящие приглашения в статусе PENDING.
     *
     * @param toUserId опциональный фильтр по идентификатору получателя
     */
    @Transactional(readOnly = true)
    public List<LocationSharingView> getOutgoingPending(Long userId, Long toUserId) {
        if (toUserId != null) {
            return locationSharingRepository.findAllByInitiator_UserIdAndTarget_UserIdAndStatus(userId, toUserId, PENDING);
        }
        return locationSharingRepository.findAllByInitiator_UserIdAndStatus(userId, PENDING);
    }

    /**
     * Возвращает идентификаторы пользователей с активным обменом геолокацией.
     */
    @Transactional(readOnly = true)
    public List<Long> findActivePartnerIds(Long userId) {
        return locationSharingRepository.findActivePartnerIds(userId);
    }

    /**
     * Возвращает все активные записи обмена, где участвует указанный пользователь.
     */
    @Transactional(readOnly = true)
    public List<LocationSharing> findAllActiveForUser(Long userId) {
        return locationSharingRepository.findAllActiveForUser(userId);
    }

    /**
     * Переводит запись в статус ACTIVE и проставляет время принятия.
     */
    private LocationSharing activate(LocationSharing sharing) {
        sharing.setStatus(ACTIVE);
        sharing.setAcceptedAt(LocalDateTime.now());
        return locationSharingRepository.save(sharing);
    }

    /**
     * Загружает запись по идентификатору или возвращает 404.
     */
    private LocationSharing loadSharing(Long sharingId) {
        return locationSharingRepository.findById(sharingId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "sharing not found"));
    }

    /**
     * Проверяет, есть ли уже активный обмен между пользователями (в любом направлении).
     */
    private boolean hasActiveSharing(Long userId, Long friendUserId) {
        return locationSharingRepository
                .existsByStatusAndInitiator_UserIdAndTarget_UserId(ACTIVE, userId, friendUserId)
                || locationSharingRepository
                .existsByStatusAndInitiator_UserIdAndTarget_UserId(ACTIVE, friendUserId, userId);
    }

    /**
     * Проверяет условия перед созданием приглашения.
     */
    private void validate(Long initiatorUserId, Long friendUserId) {
        if (initiatorUserId.equals(friendUserId)) {
            throw new LogicException("exception.location.sharing.self");
        }
        if (!friendService.isFriends(initiatorUserId, friendUserId)) {
            throw new LogicException("exception.location.sharing.not.friends");
        }
        if (hasActiveSharing(initiatorUserId, friendUserId)) {
            throw new LogicException("exception.location.sharing.already.active");
        }
    }
}
