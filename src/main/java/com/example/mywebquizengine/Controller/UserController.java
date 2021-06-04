package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static com.example.mywebquizengine.Controller.QuizController.getAuthUser;

@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;


    @GetMapping(path = "/profile")
    public String getProfile(Model model , Authentication authentication) {

        User user = getAuthUser(authentication, userService);
        model.addAttribute("user", user);
        return "profile";
    }



    @GetMapping(path = "/activate/{activationCode}")
    public String activate(@PathVariable String activationCode) {
        userService.activateAccount(activationCode);
        return "singin";
    }

    @PostMapping(path = "/api/register")
    public String checkIn(@Valid User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setAvatar("default");
            user.grantAuthority(Role.ROLE_USER);
            userService.saveUser(user);
            return "reg";
        } catch (Exception e){
            return "error";
            //throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(path = "/update/userinfo/password", consumes ={"application/json"} )
    public void tryToChangePass(Authentication authentication) {
        //model.addAttribute("notification", "");
        User user = getAuthUser(authentication, userService);
        userService.sendCodeForChangePassword(user);
        //return "";
    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes ={"application/json"} )
    public void tryToChangePass(@RequestBody User in) {
        //model.addAttribute("notification", "");
        User user;
        if (userService.reloadUser(in.getUsername()).isPresent()) {
            user = userService.reloadUser(in.getUsername()).get();
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        userService.sendCodeForChangePassword(user);
        //return "";
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Model model, Authentication authentication) {

        User user = userService.castToUser((OAuth2AuthenticationToken) authentication);

//        Authentication authentication1 = SecurityContextHolder.getContext().getAuthentication();
        userService.tryToSaveUser(user); // save if not exist (registration)

        return "home";
    }

    //@Transactional
    @GetMapping(path = "/updatepass/{changePasswordCode}")
    public String changePasswordPage(@PathVariable String changePasswordCode) {
        //User userLogin = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        User user = userService.getUserViaChangePasswordCode(changePasswordCode);

        return "changePassword";

        //user.setUsername(userLogin.getUsername());
        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        //userService.updatePassword(user);
    }

    @RequestMapping(value = "/userss")
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping(path = "/signin")
    public String singin() {

        /*OAuth2AuthorizedClient client = authorizedClientService
                .loadAuthorizedClient(
                        authentication.getAuthorizedClientRegistrationId(),
                        authentication.getName());*/


        return "singin";
    }


    @Transactional
    @PutMapping(path = "/pass", consumes ={"application/json"})
    public String changePassword(@RequestBody User user, Authentication authentication) {

        User userLogin = getAuthUser(authentication, userService);

        user.setUsername(userLogin.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setChangePasswordCode(UUID.randomUUID().toString());
        userService.updatePassword(user);

        return "changePassword";
    }


    @Transactional
    @PutMapping(path = "/updatepass/{activationCode}", consumes ={"application/json"})
    public String changePassword_2(@RequestBody User in, @PathVariable String activationCode) {

        User user = userService.getUserViaChangePasswordCode(activationCode);

        //User userLogin = getAuthUser(authentication, userService);

        //in.setUsername(userLogin.getUsername());
        user.setPassword(passwordEncoder.encode(in.getPassword()));
        user.setChangePasswordCode(UUID.randomUUID().toString());
        userService.updatePassword(user);

       // User user = userService.reloadUser(in.getUsername()).get();
        return "changePassword";
    }



    @Transactional
    @PutMapping(path = "/update/user/{username}", consumes={"application/json"})
    public void changeUser(@PathVariable String username, @RequestBody User user, Authentication authentication) {

        User userLogin = getAuthUser(authentication, userService);
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

    @PostMapping(path = "/checkyandex")
    public void checkyandex(){
        System.out.println("Пришло уведомление");
    }


}
