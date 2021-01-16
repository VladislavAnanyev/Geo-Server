package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {
    @Override
    Optional<User> findById(String s);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET FIRST_NAME = :firstname, LAST_NAME = :lastname WHERE USERNAME = :username", nativeQuery = true)
    void updateUserInfo(String firstname, String lastname, String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET PASSWORD = :password WHERE USERNAME = :username", nativeQuery = true)
    void changePassword(String password, String username);


    @Query(value = "SELECT * FROM USERS WHERE ACTIVATION_CODE = :activationCode",nativeQuery = true )
    User findByActivationCode(String activationCode);

    @Modifying
    @Transactional
    @Query(value = "UPDATE USERS SET enabled = true WHERE USERNAME = :username", nativeQuery = true)
    void activateAccount(String username);
}
