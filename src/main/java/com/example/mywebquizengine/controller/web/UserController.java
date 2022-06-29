package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.auth.AuthFacade;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.util.RegistrationModelMapper;
import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.common.common.Client;
import com.example.mywebquizengine.request.RequestFacade;
import com.example.mywebquizengine.user.model.dto.AuthUserView;
import com.example.mywebquizengine.request.model.domain.Request;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.auth.model.dto.input.ChangePasswordRequest;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationRequest;
import com.example.mywebquizengine.request.service.RequestService;
import com.example.mywebquizengine.auth.service.AuthService;
import com.example.mywebquizengine.user.service.UserService;
import com.example.mywebquizengine.common.utils.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthFacade authFacade;

    @Autowired
    private RequestFacade requestFacade;

    @GetMapping(path = "/friends")
    public String getFriends(Model model, @ApiIgnore @AuthenticationPrincipal User authUser) {

        model.addAttribute("friends", userService.findMyFriends(authUser.getUserId()));
        return "friends";
    }

    @DeleteMapping(path = "/friend/{userId}")
    @ResponseBody
    public void deleteFriend(@PathVariable Long userId, @ApiIgnore @AuthenticationPrincipal User authUser) {
        userService.deleteFriend(userId, authUser.getUserId());
    }

    @GetMapping(path = "/profile")
    public String getProfile(Model model, @AuthenticationPrincipal User authUser) {

        AuthUserView user = userService.getAuthUser(authUser.getUserId());
        model.addAttribute("user", user);
        model.addAttribute("balance", user.getBalance());

        return "profile";
    }

    @GetMapping(path = "/jwt")
    @ResponseBody
    public String getJwtToken(@AuthenticationPrincipal User authUser) {
        return jwtUtil.generateToken(userService.loadUserByUserId(authUser.getUserId()));
    }

    @GetMapping(path = "/authuser")
    @ResponseBody
    public String getAuthUsername(@AuthenticationPrincipal User authUser) {
        return authUser.getUsername();
    }

    @GetMapping(path = "/getbalance")
    @ResponseBody
    public Integer getBalance(@AuthenticationPrincipal User authUser) {
        User user = userService.loadUserByUserId(authUser.getUserId());
        return user.getBalance();
    }

    @GetMapping(path = "/activate/{activationCode}")
    public String activate(@PathVariable String activationCode) {
        userService.activateAccount(activationCode);
        return "singin";
    }

    @PostMapping(path = "/register")
    public String checkIn(@Valid RegistrationRequest registrationRequest) {
        RegistrationModel registrationModel = RegistrationModelMapper.map(registrationRequest);
        authService.saveUser(registrationModel, RegistrationType.BASIC);
        return "reg";
    }

    @PostMapping("/user/password/request")
    public void tryToChangePassword(@RequestBody String username) {
        authService.setChangePassword(username, Client.WEB);
    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Authentication authentication, Model model) {
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
    public String changePasswordUsingCode(@RequestBody ChangePasswordRequest request, @PathVariable String changePasswordCode) {
        request.setCode(changePasswordCode);
        authFacade.updatePassword(request.getUsername(), request.getCode(), request.getPassword());
        return "changePassword";
    }

    @Transactional
    @PutMapping(path = "/update/user/{userId}", consumes = {"application/json"})
    @PreAuthorize(value = "#authUser.userId.equals(#userId)")
    public void changeUser(@PathVariable Long userId, @RequestBody User user, @AuthenticationPrincipal User authUser) {
        userService.updateUser(user.getLastName(), user.getFirstName(), userId);
    }

    @GetMapping(path = "/about/{userId}")
    public String getInfoAboutUser(Model model, @PathVariable Long userId, @AuthenticationPrincipal User authUser) {

        if (authUser != null && userId.equals(userService.loadUserByUserId(authUser.getUserId()).getUserId())) {
            return "redirect:/profile";
        } else {
            User user = userService.loadUserByUserId(userId);
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
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal User authUser) {
        requestFacade.sendRequest(
                request.getMeeting().getMeetingId(),
                authUser.getUserId(),
                request.getTo().getUserId(),
                request.getMessage().getContent()
        );
    }

    @GetMapping(path = "/requests")
    public String getMyRequests(Model model, @AuthenticationPrincipal User authUser) {

        model.addAttribute("myUsername", authUser.getUserId());
        model.addAttribute("meetings", requestFacade.getMyRequests(authUser.getUserId()));

        return "requests";
    }

    @PostMapping(path = "/acceptRequest")
    @ResponseBody
    public Long acceptRequest(@RequestBody Request request, @AuthenticationPrincipal User authUser) {
        return requestFacade.acceptRequest(request.getRequestId(), authUser.getUserId());
    }

    @PostMapping(path = "/rejectRequest")
    @ResponseBody
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal User authUser) {
        requestFacade.rejectRequest(requestId.getRequestId(), authUser.getUserId());
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
