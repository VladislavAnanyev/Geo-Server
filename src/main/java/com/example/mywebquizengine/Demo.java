package com.example.mywebquizengine;

import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class Demo implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository ;

    @Autowired
    private PasswordEncoder passwordEncoder ;

    @Override
    public void run(String... args) {
        User user = new User();
        user.setUsername("application");
        user.setPassword(passwordEncoder.encode("password"));
        user.setEmail("a.vlad.v@yandex.ru");
        user.setFirstName("Владислав");
        user.setLastName("Ананьев");
        user.setEnabled(true);
        user.grantAuthority(Role.ROLE_ADMIN);
        userRepository.save(user);

    }
}
