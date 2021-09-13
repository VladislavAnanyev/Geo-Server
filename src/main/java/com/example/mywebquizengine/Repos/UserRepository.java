package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Projection.UserForMessageView;
import com.example.mywebquizengine.Model.Projection.UserView;
import com.example.mywebquizengine.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String>, JpaRepository<User, String> {
    @Override
    Optional<User> findById(String s);


    UserForMessageView findByUsername(String username);

    UserView findAllByUsername(String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET FIRST_NAME = :firstname, LAST_NAME = :lastname WHERE USERNAME = :username", nativeQuery = true)
    void updateUserInfo(String firstname, String lastname, String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET PASSWORD = :password, CHANGE_PASSWORD_CODE = :changePasswordCode WHERE USERNAME = :username", nativeQuery = true)
    void changePassword(String password, String username, String changePasswordCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET AVATAR = :avatarName WHERE USERNAME = :username", nativeQuery = true)
    void setAvatar(String avatarName, String username);


    @Query(value = "SELECT * FROM USERS WHERE ACTIVATION_CODE = :activationCode",nativeQuery = true )
    User findByActivationCode(String activationCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET status = true WHERE USERNAME = :username", nativeQuery = true)
    void activateAccount(String username);


    @Query(value = "SELECT * FROM USERS WHERE CHANGE_PASSWORD_CODE = :changePasswordCode", nativeQuery = true )
    Optional<User> findByChangePasswordCode(String changePasswordCode);
}
