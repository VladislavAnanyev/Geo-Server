package com.example.meetings.user.service;

import com.example.meetings.user.model.domain.Role;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.domain.UserRole;
import com.example.meetings.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис управления ролями пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

    /**
     * Возвращает список ролей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return уникальный список ролей пользователя
     */
    public List<Role> getRoles(Long userId) {
        return userRoleRepository.findRolesByUserId(userId).stream()
                .map(r -> {
                    try {
                        return Role.valueOf(r);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(java.util.Objects::nonNull)
                .distinct()
                .toList();
    }

    /**
     * Назначает роль пользователя по умолчанию, если у него ещё нет ролей.
     *
     * @param user пользователь
     */
    @Transactional
    public void ensureDefaultRole(User user) {
        List<String> roles = userRoleRepository.findRolesByUserId(user.getUserId());
        if (roles.isEmpty()) {
            userRoleRepository.save(new UserRole().setUser(user).setRole(Role.ROLE_USER));
        }
    }

    /**
     * Синхронизирует премиум-роль пользователя с активностью подписки.
     *
     * @param user    пользователь
     * @param premium {@code true}, если у пользователя активна премиум-подписка
     */
    @Transactional
    public void syncPremiumRole(User user, boolean premium) {
        Long userId = user.getUserId();
        boolean hasPremium = userRoleRepository.existsByUserUserIdAndRole(userId, Role.ROLE_PREMIUM);
        if (premium && !hasPremium) {
            userRoleRepository.save(new UserRole().setUser(user).setRole(Role.ROLE_PREMIUM));
        } else if (!premium && hasPremium) {
            userRoleRepository.deleteByUserUserIdAndRole(userId, Role.ROLE_PREMIUM);
        }
    }
}

