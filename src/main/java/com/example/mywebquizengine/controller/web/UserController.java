package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.projection.UserCommonView;
import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.RegistrationType;
import com.example.mywebquizengine.model.request.Request;
import com.example.mywebquizengine.model.userinfo.User;
import com.example.mywebquizengine.security.ActiveUserStore;
import com.example.mywebquizengine.service.AuthService;
import com.example.mywebquizengine.service.JWTUtil;
import com.example.mywebquizengine.service.RequestService;
import com.example.mywebquizengine.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import freemarker.template.TemplateModelException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @GetMapping(path = "/friends")
    public String getFriends(Model model, @ApiIgnore @AuthenticationPrincipal User authUser) {

        model.addAttribute("friends", userService.findMyFriends(authUser.getUsername()));
        return "friends";
    }

    @DeleteMapping(path = "/friend/{username}")
    @ResponseBody
    public void deleteFriend(@PathVariable String username, @ApiIgnore @AuthenticationPrincipal User authUser) {
        userService.deleteFriend(username, authUser.getUsername());
    }

    @GetMapping(path = "/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User authUser) {

        UserView user = authService.getAuthUser(authUser.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("balance", user.getBalance());

        return "profile";
    }

    @GetMapping(path = "/jwt")
    @ResponseBody
    public String getJwtToken(@AuthenticationPrincipal User authUser) {
        return jwtUtil.generateToken(userService.loadUserByUsername(authUser.getUsername()));
    }

    @GetMapping(path = "/authuser")
    @ResponseBody
    public String getAuthUsername(@AuthenticationPrincipal User authUser) {
        return authUser.getUsername();
    }


    @GetMapping(path = "/getbalance")
    @ResponseBody
    public Integer getBalance(@AuthenticationPrincipal User authUser) {
        User user = userService.loadUserByUsername(authUser.getUsername());
        return user.getBalance();
    }


    @GetMapping(path = "/activate/{activationCode}")
    public String activate(@PathVariable String activationCode) {
        userService.activateAccount(activationCode);
        return "singin";
    }

    @PostMapping(path = "/register")
    public String checkIn(@Valid User user) {

        authService.processCheckIn(user, RegistrationType.BASIC);
        return "reg";

    }

    @PostMapping(path = "/update/userinfo/password", consumes = {"application/json"})
    public void tryToChangePassWithAuth(@AuthenticationPrincipal User authUser) {

        //User user = userService.loadUserByUsernameProxy(authUser.getUsername());
        authService.sendCodeForChangePassword(authUser.getUsername());

    }

    @GetMapping("/loggedUsers")
    @ResponseBody
    public ArrayList<String> getLoggedUsers(Locale locale, Model model) {

        return (ArrayList<String>) activeUserStore.getUsers();
    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes = {"application/json"})
    public void tryToChangePassWithoutAuth(@RequestBody User in) {

        //User user = userService.loadUserByUsername(in.getUsername());

        authService.sendCodeForChangePassword(in.getUsername());

    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Authentication authentication, Model model) throws TemplateModelException, IOException {
        return "home";
    }


    @GetMapping(path = "/updatepass/{changePasswordCode}")
    public String changePasswordPage(@PathVariable String changePasswordCode) {
        userService.getUserViaChangePasswordCode(changePasswordCode);
        return "changePassword";
    }

    @GetMapping(path = "/signin")
    public String signIn() {
        return "singin";
    }

    @PutMapping(path = "/updatepass/{changePasswordCode}", consumes = {"application/json"})
    public String changePasswordUsingCode(@RequestBody User in, @PathVariable String changePasswordCode) {
        in.setChangePasswordCode(changePasswordCode);
        authService.updatePassword(in);

        return "changePassword";
    }


    @Transactional
    @PutMapping(path = "/update/user/{username}", consumes = {"application/json"})
    @PreAuthorize(value = "#authUser.username.equals(#username)")
    public void changeUser(@PathVariable String username, @RequestBody User user, @AuthenticationPrincipal User authUser) {
        userService.updateUser(user.getLastName(), user.getFirstName(), username);
    }

    @GetMapping(path = "/about/{username}")
    public String getInfoAboutUser(Model model, @PathVariable String username, @AuthenticationPrincipal User authUser) {

        if (authUser != null && username.equals(userService.loadUserByUsername(authUser.getUsername()).getUsername())) {
            return "redirect:/profile";
        } else {
            User user = userService.loadUserByUsername(username);
            model.addAttribute("user", user);
            return "user";
        }

    }


    @GetMapping(path = "/getUserList")
    @ResponseBody
    public ArrayList<User> getUserList() {
        return userService.getUserList();
    }


    @PostMapping(path = "/sendRequest")
    @ResponseBody
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal User authUser) throws JsonProcessingException, ParseException, NoSuchAlgorithmException {
        requestService.sendRequest(request, authUser.getUsername());
    }

    @GetMapping(path = "/requests")
    public String getMyRequests(Model model, @AuthenticationPrincipal User authUser) {

        model.addAttribute("myUsername", authUser.getUsername());
        model.addAttribute("meetings", requestService.getMyRequests(authUser.getUsername()));

        return "requests";
    }


    @PostMapping(path = "/acceptRequest")
    @ResponseBody
    public Long acceptRequest(@RequestBody Request request, @AuthenticationPrincipal User authUser) throws JsonProcessingException, ParseException, NoSuchAlgorithmException {
        return requestService.acceptRequest(request.getId(), authUser.getUsername());
    }

    @PostMapping(path = "/rejectRequest")
    @ResponseBody
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal User authUser) {
        requestService.rejectRequest(requestId.getId(), authUser.getUsername());
    }

    @GetMapping(path = "/testConnection")
    @ResponseBody
    public String testConnection() {
        return "OK";
    }

    @GetMapping(path = "/")
    public String getHome() {
        return "home";
    }

    @GetMapping(path = "/reg")
    public String reg() {
        return "reg";
    }

}
