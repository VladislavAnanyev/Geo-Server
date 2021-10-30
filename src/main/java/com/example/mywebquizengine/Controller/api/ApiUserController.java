package com.example.mywebquizengine.Controller.api;

import com.example.mywebquizengine.Model.Projection.UserCommonView;
import com.example.mywebquizengine.Model.Projection.UserView;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Model.UserInfo.AuthRequest;
import com.example.mywebquizengine.Model.UserInfo.AuthResponse;
import com.example.mywebquizengine.Model.UserInfo.GoogleToken;
import com.example.mywebquizengine.Service.UserService;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
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
    public List<UserCommonView> getFriends(@AuthenticationPrincipal Principal principal) {
        return userService.findMyFriends(principal);
    }

    @GetMapping(path = "/findbyid")
    public UserCommonView getUserById(@RequestParam String username) {
        return userService.getUserView(username);

    }

    @PostMapping(path = "/signin")
    public AuthResponse jwtSignIn(@RequestBody AuthRequest authRequest) {

        return userService.signInViaJwt(authRequest);


    }



    @PostMapping(path = "/signup")
    //@ResponseBody
    public AuthResponse signup(@RequestBody User user) {
        return userService.signUpViaJwt(user);
    }




    @PostMapping(path = "/googleauth")
    public AuthResponse googleJwt(@RequestBody GoogleToken token) throws GeneralSecurityException, IOException {
        return userService.signinViaGoogleToken(token);
    }


    @GetMapping(path = "/authuser")
    public UserView getApiAuthUser(@AuthenticationPrincipal Principal principal) {
        return userService.getAuthUser(principal);
    }


    @PostMapping(path = "/upload")
    public void uploadPhoto(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Principal principal) {
        userService.uploadPhoto(file, principal);
    }

    @Transactional
    @PutMapping(path = "/user", consumes={"application/json"})
    public void changeUser(@RequestBody User user,
                           @AuthenticationPrincipal Principal principal) {
        userService.updateUser(user.getLastName(), user.getFirstName(), principal.getName());
    }

}
