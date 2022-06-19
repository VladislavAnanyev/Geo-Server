package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.userinfo.dto.output.ProfileView;
import com.example.mywebquizengine.model.userinfo.dto.output.UserCommonView;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthUserView;
import com.example.mywebquizengine.model.userinfo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long>, JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

    boolean existsByUsername(String username);

    UserCommonView findByUserId(Long userId);

    AuthUserView findAllByUserId(Long userId);

    ProfileView findUserByUserIdOrderByUsernameAscPhotosAsc(Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET FIRST_NAME = :firstname, LAST_NAME = :lastname WHERE USER_ID = :userId", nativeQuery = true)
    void updateUserInfo(String firstname, String lastname, Long userId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET PASSWORD = :password, CHANGE_PASSWORD_CODE = :changePasswordCode WHERE USERNAME = :username", nativeQuery = true)
    void changePassword(String password, String username, String changePasswordCode);

    @Query(value = "SELECT * FROM USERS WHERE ACTIVATION_CODE = :activationCode",nativeQuery = true )
    User findByActivationCode(String activationCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET status = true WHERE USERNAME = :userId", nativeQuery = true)
    void activateAccount(Long userId);

    @Query(value = "SELECT * FROM USERS WHERE CHANGE_PASSWORD_CODE = :changePasswordCode", nativeQuery = true )
    Optional<User> findByChangePasswordCode(String changePasswordCode);

    Optional<User> findByChangePasswordCodeAndUsername(String changePasswordCode, String username);

    boolean existsByChangePasswordCodeAndUsername(String changePasswordCode, String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET CHANGE_PASSWORD_CODE = :mes WHERE USERNAME = :username", nativeQuery = true)
    void setChangePasswordCode(String username, String mes);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET ONLINE = :status WHERE USERNAME = :username", nativeQuery = true)
    void setOnline(String username, String status);

    @Transactional
    @Query(value = "select ONLINE from USERS WHERE USERNAME = :username", nativeQuery = true)
    String getOnline(String username);

    List<UserCommonView> findUsersByFriendsUserId(Long userId);
}
