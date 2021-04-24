package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.Optional;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping(path = "/profile")
    public String getProfile(Model model) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> nowUser = userService.reloadUser(user.getUsername());
        model.addAttribute("user", nowUser.get());
        return "profile";
    }

    @GetMapping(path = "/activate/{activationCode}")
    public String activate(@PathVariable String activationCode) {
        userService.activateAccount(activationCode);
        return "login";
    }

    @PostMapping(path = "/api/register")
    public String checkIn(@Valid User user) {
        if (user.getEmail().contains(".")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setAvatar("default");
            user.grantAuthority(Role.ROLE_USER);
            userService.saveUser(user);
            return "reg";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @Transactional
    @PutMapping(path = "/update/userinfo/password", consumes={"application/json"})
    @ResponseBody
    public void changePassword(Model model, @RequestBody User user) {
        User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (user.getUsername().equals(userLogin.getUsername())) {
            user.setUsername(userLogin.getUsername());
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userService.updatePassword(user);
        }
    }

    @Transactional
    @PutMapping(path = "/update/user/{username}", consumes={"application/json"})
    public void changeUser(@PathVariable String username, @RequestBody User user) {
        User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userLogin.getUsername().equals(username)) {
            userService.updateUser(user.getLastName(), user.getFirstName(), username);
        }
    }

    @GetMapping(path = "/about/{username}")
    public String getInfoAboutUser(Model model, @PathVariable String username) {
        Optional<User> user = userService.reloadUser(username);
        model.addAttribute("user", user.get());
        return "user";
    }
}
