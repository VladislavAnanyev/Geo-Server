package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Controller.api.ApiGeoController;
import com.example.mywebquizengine.Controller.api.ApiRequestController;
import com.example.mywebquizengine.Model.*;
import com.example.mywebquizengine.Repos.*;
import com.example.mywebquizengine.Security.ActiveUserStore;
import com.example.mywebquizengine.Service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import freemarker.template.TemplateModelException;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
/*import javax.validation.Valid;*/
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;


@Controller
public class UserController {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RequestRepository requestRepository;

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private ActiveUserStore activeUserStore;

    @Autowired
    private ApiGeoController apiGeoController;

    @Autowired
    private ApiRequestController apiRequestController;


    @GetMapping(path = "/profile")
    public String getProfile(Model model , @AuthenticationPrincipal Principal principal) {

        User user = userService.loadUserByUsername(principal.getName());
        model.addAttribute("user", user);

        model.addAttribute("balance", user.getBalance());

        return "profile";
    }

    @GetMapping(path = "/authuser")
    @ResponseBody
    public String getAuthUser(@AuthenticationPrincipal Principal principal) {
       // Authentication authentication2 = SecurityContextHolder.getContext().getAuthentication();
        return userService.loadUserByUsernameProxy(principal.getName()).getUsername();
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
    public String checkIn(/*@Valid */User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setEnabled(false);
            user.setAvatar("https://" + hostname + "/img/default.jpg");
            user.grantAuthority(Role.ROLE_USER);
            userService.saveUser(user);
            return "reg";
        } catch (Exception e){
            return "error";
        }
    }

    @PostMapping(path = "/update/userinfo/password", consumes ={"application/json"} )
    public void tryToChangePassWithAuth(@AuthenticationPrincipal Principal principal) {

        User user = userService.loadUserByUsernameProxy(principal.getName());
        userService.sendCodeForChangePassword(user);

    }

    @GetMapping("/loggedUsers")
    @ResponseBody
    public ArrayList<String> getLoggedUsers(Locale locale, Model model) {

        return (ArrayList<String>) activeUserStore.getUsers();
    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes ={"application/json"} )
    public void tryToChangePassWithoutAuth(@RequestBody User in) {

        User user = userService.loadUserByUsername(in.getUsername());


        userService.sendCodeForChangePassword(user);

    }

    @GetMapping("/loginSuccess")
    //@After("signin()")
    public String getLoginInfo(Authentication authentication, Model model) throws TemplateModelException, IOException {



        return "home";
    }


    @GetMapping(path = "/updatepass/{changePasswordCode}")
    public String changePasswordPage(@PathVariable String changePasswordCode) {
        User user = userService.getUserViaChangePasswordCode(changePasswordCode);
        return "changePassword";
    }

    @RequestMapping(value = "/userss")
    public Principal user(Principal principal) {
        return principal;
    }

    @GetMapping(path = "/signin")
    public String singin() {

        return "singin";
    }

    @GetMapping(path = "/singin")
    public String singin2() {

        return "redirect:/profile";
    }


    @Transactional
    @PutMapping(path = "/pass", consumes ={"application/json"})
    public String changePassword(@RequestBody User user, @AuthenticationPrincipal Principal principal) {

        User userLogin = userService.loadUserByUsername(principal.getName());

        user.setUsername(userLogin.getUsername());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setChangePasswordCode(UUID.randomUUID().toString());
        userService.updatePassword(user);

        return "changePassword";
    }


    @Transactional
    @PutMapping(path = "/updatepass/{changePasswordCode}", consumes ={"application/json"})
    public String changePasswordUsingCode(@RequestBody User in, @PathVariable String changePasswordCode) {

        User user = userService.getUserViaChangePasswordCode(changePasswordCode);

        user.setPassword(passwordEncoder.encode(in.getPassword()));
        user.setChangePasswordCode(UUID.randomUUID().toString());
        userService.updatePassword(user);

        return "changePassword";
    }



    @Transactional
    @PutMapping(path = "/update/user/{username}", consumes={"application/json"})
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

    @PostMapping(path = "/checkyandex")
    @ResponseBody
    @Transactional
    public void checkyandex(String notification_type, String operation_id, Number amount, Number withdraw_amount,
                            String currency, String datetime, String sender, Boolean codepro, String label,
                            String sha1_hash, Boolean test_notification, Boolean unaccepted, String lastname,
                            String firstname, String fathersname, String email, String phone, String city,
                            String street, String building, String suite, String flat, String zip) throws NoSuchAlgorithmException {

        userService.processPayment(notification_type, operation_id, amount, withdraw_amount, currency, datetime, sender, codepro, label, sha1_hash, test_notification, unaccepted, lastname, firstname, fathersname, email, phone, city, street, building, suite, flat, zip);

    }



    @GetMapping(path = "/getUserList")
    @ResponseBody
    public ArrayList<User> getUserList() {
        return userService.getUserList();
    }


    // UserService is required because this method is static, but UserService non-static

    @PostMapping(path = "/sendRequest")
    @ResponseBody
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) throws JsonProcessingException, ParseException {
        apiRequestController.sendRequest(request, principal);
    }

    @GetMapping(path = "/requests")
    public String getMyRequests(Model model, @AuthenticationPrincipal Principal principal) {

        User authUser = userService.loadUserByUsername(principal.getName());

        model.addAttribute("myUsername", authUser.getUsername());

        model.addAttribute("meetings",
                requestRepository.findAllByToUsernameAndStatus(authUser.getUsername(), "PENDING"));

        return "requests";
    }


    @PostMapping(path = "/acceptRequest")
    @ResponseBody
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public Long acceptRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        return apiRequestController.acceptRequest(requestId, principal);
    }

    @PostMapping(path = "/rejectRequest")
    @ResponseBody
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        apiRequestController.rejectRequest(requestId, principal);
    }



    @GetMapping(path = "/testConnection")
    @ResponseBody
    public String testConnection(/*@AuthenticationPrincipal Principal principal*/) {
/*        if (!activeUserStore.getUsers().contains(principal.getName())) {
            activeUserStore.getUsers().add(principal.getName());
            userRepository.setOnline(principal.getName(), "true");
        }*/

        /*if (userRepository.getOnline(principal.getName()).equals("false")) {
            userRepository.setOnline(principal.getName(), "true");
        }*/

        return "OK";
    }

    /*private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @GetMapping(path = "/swagger-ui")
    public void getSwagger(@AuthenticationPrincipal Principal principal, HttpServletRequest httpServletRequest,
                           HttpServletResponse httpServletResponse) throws IOException {

        User user = userService.loadUserByUsername(principal.getName());

        if (user.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            redirectStrategy.sendRedirect(httpServletRequest, httpServletResponse, httpServletRequest.getRequestURI());
        }
    }*/



}
