package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.*;
import com.example.mywebquizengine.Model.Chat.Dialog;
import com.example.mywebquizengine.Model.Chat.MessageStatus;
import com.example.mywebquizengine.MywebquizengineApplication;
import com.example.mywebquizengine.Repos.*;
import com.example.mywebquizengine.Security.ActiveUserStore;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.PaymentServices;
import com.example.mywebquizengine.Service.UserService;
import freemarker.template.TemplateModelException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;


@Controller
public class UserController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RequestRepository requestRepository;

    @Value("${hostname}")
    private String hostname;

    @Autowired
    private ChatController chatController;

    @Autowired
    private UserRepository userRepository;


    @Autowired
    private ActiveUserStore activeUserStore;


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
    public String checkIn(@Valid User user) {
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

        User user = userService.castToUser((OAuth2AuthenticationToken) authentication);

        userService.tryToSaveUser(user); // save if not exist (registration)

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



        if (username.equals(userService.loadUserByUsername(principal.getName()).getUsername())) {
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
    public void sendRequest(@RequestBody Request request, @AuthenticationPrincipal Principal principal) {
        request.setSender(userService.loadUserByUsername(principal.getName()));

        Long dialogId = chatController.checkDialog(request.getTo(), principal);

        Dialog dialog = new Dialog();
        dialog.setDialogId(dialogId);
        request.getMessage().setDialog(dialog);
        request.getMessage().setSender(userService.loadUserByUsernameProxy(principal.getName()));
        request.getMessage().setStatus(MessageStatus.DELIVERED);
        request.getMessage().setTimestamp(new GregorianCalendar());

        requestRepository.save(request);
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

        User authUser = userService.loadUserByUsername(principal.getName());


        Request request = requestRepository.findById(requestId.getId()).get();
        request.setStatus("ACCEPTED");

        authUser.addFriend(request.getSender());
        requestRepository.save(request);
        Long dialog_id = messageService.checkDialog(request.getSender(), principal.getName());

        if (dialog_id == null) {
            Dialog dialog = new Dialog();
            //  Set<User> users = new HashSet<>();
            dialog.addUser(userService.loadUserByUsername(request.getSender().getUsername()));
            dialog.addUser(authUser);
//            users.add(userService.loadUserByUsername(user.getUsername()));
//            users.add(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()));
            //dialog.setUsers(users);
            dialogRepository.save(dialog);
            return dialog.getDialogId();
        } else {
            return dialog_id;
        }
    }

    @PostMapping(path = "/rejectRequest")
    @ResponseBody
    //@PreAuthorize(value = "!#principal.name.equals(#user.username)")
    public void rejectRequest(@RequestBody Request requestId, @AuthenticationPrincipal Principal principal) {
        Request request = requestRepository.findById(requestId.getId()).get();
        request.setStatus("REJECTED");
        requestRepository.save(request);
    }



    @GetMapping(path = "/testConnection")
    @ResponseBody
    public String testConnection(@AuthenticationPrincipal Principal principal) {
/*        if (!activeUserStore.getUsers().contains(principal.getName())) {
            activeUserStore.getUsers().add(principal.getName());
            userRepository.setOnline(principal.getName(), "true");
        }*/

        if (userRepository.getOnline(principal.getName()).equals("false")) {
            userRepository.setOnline(principal.getName(), "true");
        }

        return "OK";
    }



}
