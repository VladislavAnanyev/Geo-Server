package com.example.mywebquizengine;

import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;

@Component
public class Demo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws UnknownHostException {
        User user = new User();
        user.setUsername("application");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("a.vlad.v@yandex.ru");
        user.setFirstName("Владислав");
        user.setLastName("Ананьев");
        user.setEnabled(true);
        user.setStatus(true);
        user.setAvatar("default");
        user.setChangePasswordCode(UUID.randomUUID().toString());
        user.grantAuthority(Role.ROLE_ADMIN);
        userRepository.save(user);

        User user1 = userService.reloadUser("mad_maximuus").get();
        User user2 = userService.reloadUser("salam_moleculam").get();

        user1.setPassword(passwordEncoder.encode("12345"));
        user2.setPassword(passwordEncoder.encode("12345"));

    }
}
