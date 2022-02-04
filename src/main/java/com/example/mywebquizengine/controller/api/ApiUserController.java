package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.projection.ProfileView;
import com.example.mywebquizengine.model.projection.UserCommonView;
import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api")
public class ApiUserController {

    @Autowired
    private UserService userService;

    @GetMapping(path = "/friends")
    public List<UserCommonView> getFriends(@ApiIgnore @AuthenticationPrincipal Principal principal) {
        return userService.findMyFriends(principal.getName());
    }

    @DeleteMapping(path = "/friend/{username}")
    public void deleteFriend(@PathVariable String username, @ApiIgnore @AuthenticationPrincipal Principal principal) {
        userService.deleteFriend(username, principal.getName());
    }

    @GetMapping(path = "/findbyid")
    public UserCommonView getUserById(@RequestParam String username) {
        return userService.getUserView(username);
    }

    @PostMapping(path = "/signin")
    public AuthResponse jwtSignIn(@RequestBody AuthRequest authRequest) {
        return userService.signInViaApi(authRequest);
    }

    @PostMapping(path = "/signup")
    public AuthResponse signup(@Valid @RequestBody RegistrationRequest registrationRequest)  {
        User user = new User();
        user.setUsername(registrationRequest.getUsername());
        user.setFirstName(registrationRequest.getFirstName());
        user.setLastName(registrationRequest.getLastName());
        user.setPassword(registrationRequest.getPassword());
        user.setEmail(registrationRequest.getEmail());

        userService.processCheckIn(user, RegistrationType.BASIC);
        return userService.getJwtToken(user);
    }

    @PostMapping(path = "/googleauth")
    public AuthResponse googleJwt(@RequestBody GoogleToken token, HttpServletRequest request) throws GeneralSecurityException, IOException, ServletException {
        request.logout();
        return userService.signinViaGoogleToken(token);
    }

    @GetMapping(path = "/authuser")
    public UserView getApiAuthUser(@ApiIgnore @AuthenticationPrincipal Principal principal) {
        return userService.getAuthUser(principal.getName());
    }

    @GetMapping(path = "/user/{username}/profile")
    public ProfileView getProfile(@PathVariable String username) {
        return userService.getUserProfileById(username);
    }


    @PutMapping(path = "/user", consumes = {"application/json"})
    public void changeUser(@RequestBody User user,
                           @ApiIgnore @AuthenticationPrincipal Principal principal) {
        userService.updateUser(user.getLastName(), user.getFirstName(), principal.getName());
    }

    @PostMapping(path = "/user/send-change-password-code")
    public void sendChangePasswordCodeWithoutAuth(@RequestParam String username) {
        userService.sendCodeForChangePasswordFromPhone(username);
    }

    @PutMapping(path = "/user/password")
    public void changePassword(@RequestBody User user) {
        userService.updatePassword(user);
    }

    @PostMapping(path = "/user/verify-password-code")
    public void verifyChangePasswordCode(@RequestBody VerifyCodeRequest verifyCodeRequest) {
        userService.getUserViaChangePasswordCodePhoneApi(
                verifyCodeRequest.getUsername(),
                verifyCodeRequest.getCode()
        );
    }

    @GetMapping(path = "/user/check-username")
    public Boolean checkExistUser(@RequestParam String username) {
        return userService.checkForExistUser(username);
    }

}
