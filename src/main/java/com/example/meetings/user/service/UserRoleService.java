package com.example.meetings.user.service;

import com.example.meetings.user.model.domain.Role;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.domain.UserRole;
import com.example.meetings.user.repository.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserRoleService {

    private final UserRoleRepository userRoleRepository;

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

    @Transactional
    public void ensureDefaultRole(User user) {
        List<String> roles = userRoleRepository.findRolesByUserId(user.getUserId());
        if (roles.isEmpty()) {
            userRoleRepository.save(new UserRole().setUser(user).setRole(Role.ROLE_USER));
        }
    }

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

