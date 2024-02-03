package com.example.meetings.user.repository;

import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);

    UserCommonView findByUserId(Long userId);

    AuthUserView findAllByUserId(Long userId);

    ProfileView findUserByUserIdOrderByUsernameAscPhotosAsc(Long userId);

    List<UserCommonView> findUsersByFriendsUserId(Long userId);
}
