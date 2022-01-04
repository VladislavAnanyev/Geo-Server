package com.example.mywebquizengine.controller.web;

import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.RegistrationType;
import com.example.mywebquizengine.model.Request;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.security.ActiveUserStore;
import com.example.mywebquizengine.service.RequestService;
import com.example.mywebquizengine.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import freemarker.template.TemplateModelException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Locale;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RequestService requestService;

    @Autowired
    private ActiveUserStore activeUserStore;


    @GetMapping(path = "/profile")
    public String getProfile(Model model, @AuthenticationPrincipal Principal principal) {

        UserView user = userService.getAuthUser(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("balance", user.getBalance());

        return "profile";
    }

    @GetMapping(path = "/authuser")
    @ResponseBody
    public String getAuthUsername(@AuthenticationPrincipal Principal principal) {
        return principal.getName();
    }


    @GetMapping(path = "/getbalance")
    @ResponseBody
    public Integer getBalance(@AuthenticationPrincipal Principal principal) {
        User user = userService.loadUserByUsername(principal.getName());
        return user.getBalance();
    }


    @GetMapping(path = "/activate/{activationCode}")
    public String activate(@PathVariable String activationCode) {
        userService.activateAccount(activationCode);
        return "singin";
    }

    @PostMapping(path = "/register")
    public String checkIn(@Valid User user) {

        userService.processCheckIn(user, RegistrationType.BASIC);
        return "reg";

    }

    @PostMapping(path = "/update/userinfo/password", consumes = {"application/json"})
    public void tryToChangePassWithAuth(@AuthenticationPrincipal Principal principal) {

        //User user = userService.loadUserByUsernameProxy(principal.getName());
        userService.sendCodeForChangePassword(principal.getName());

    }

    @GetMapping("/loggedUsers")
    @ResponseBody
    public ArrayList<String> getLoggedUsers(Locale locale, Model model) {

        return (ArrayList<String>) activeUserStore.getUsers();
    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes = {"application/json"})
    public void tryToChangePassWithoutAuth(@RequestBody User in) {

        //User user = userService.loadUserByUsername(in.getUsername());

        userService.sendCodeForChangePassword(in.getUsername());

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
        userService.updatePassword(in);

        return "changePassword";
    }


    @Transactional
    @PutMapping(path = "/update/user/{username}", consumes = {"application/json"})
    @PreAuthorize(value = "#principal.name.equals(#username)")
    public void changeUser(@PathVariable String username, @RequestBody User user, @AuthenticationPrincipal Principal principal) {
        userService.updateUser(user.getLastName(), user.getFirstName(), username);
    }

    @GetMapping(path = "/about/{username}")
    public String getInfoAboutUser(Model model, @PathVariable String username, @AuthenticationPrincipal Principal principal) {

        if (principal != null && username.equals(userService.loadUserByUsername(principal.getName()).getUsername())) {
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
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) throws JsonProcessingException, ParseException {
        requestService.sendRequest(request, principal);
    }

    @GetMapping(path = "/requests")
    public String getMyRequests(Model model, @AuthenticationPrincipal Principal principal) {

        model.addAttribute("myUsername", principal.getName());
        model.addAttribute("meetings", requestService.getMyRequests(principal.getName()));

        return "requests";
    }


    @PostMapping(path = "/acceptRequest")
    @ResponseBody
    public Long acceptRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) throws JsonProcessingException, ParseException {
        return requestService.acceptRequest(request.getId(), principal.getName());
    }

    @PostMapping(path = "/rejectRequest")
    @ResponseBody
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        requestService.rejectRequest(requestId.getId(), principal.getName());
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
