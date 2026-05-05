package com.example.meetings.user.repository;

import com.example.meetings.user.model.domain.Role;
import com.example.meetings.user.model.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    @Query(value = "SELECT role FROM USER_ROLES WHERE user_id = :userId", nativeQuery = true)
    List<Role> findRolesByUserId(@Param("userId") Long userId);
}

