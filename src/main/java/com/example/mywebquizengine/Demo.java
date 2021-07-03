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
import java.util.List;
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

        /*User user = userRepository.findById("application").get();

        List<Role> roles = new ArrayList<>();
        roles.add(Role.ROLE_USER);
        roles.add(Role.ROLE_ADMIN);

        user.setRoles(roles);

        userRepository.save(user);*/


    }
}
