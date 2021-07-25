package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.*;
import com.example.mywebquizengine.Service.JWTUtil;
import com.example.mywebquizengine.Service.PaymentServices;
import com.example.mywebquizengine.Service.UserService;
import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;
import org.aspectj.lang.annotation.After;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.UUID;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private PaymentServices paymentServices;

    @Value("${notification-secret}")
    String notification_secret;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JWTUtil jwtTokenUtil;

    @Autowired
    private QuizController quizController;


    @GetMapping(path = "/profile")
    public String getProfile(Model model , Authentication authentication) {

        User user = userService.getAuthUser(authentication);
        model.addAttribute("user", user);

        model.addAttribute("balance", user.getBalance());

        return "profile";
    }


    @GetMapping(path = "/getbalance")
    @ResponseBody
    public Integer getBalance() {
        User user = userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication());
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
            user.setAvatar("default");
            user.grantAuthority(Role.ROLE_USER);
            userService.saveUser(user);
            return "reg";
        } catch (Exception e){
            return "error";
        }
    }

    @PostMapping(path = "/update/userinfo/password", consumes ={"application/json"} )
    public void tryToChangePassWithAuth(Authentication authentication) {

        User user = userService.getAuthUser(authentication);
        userService.sendCodeForChangePassword(user);

    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes ={"application/json"} )
    public void tryToChangePassWithoutAuth(@RequestBody User in) {

        User user = userService.reloadUser(in.getUsername());


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

    /*@GetMapping(path = "/android")
    public String singin2() {
        return "singin";
    }*/

    @PostMapping(path = "/api/authuser")
    @ResponseBody
    public String test(/*HttpServletRequest httpServletRequest*/) {

        //System.out.println(WebUtils.getCookie(httpServletRequest, "SESSION").getValue());
        //return userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication());
        return "Успех";
        /*Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getName(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(jwt);*/
    }

    @PostMapping(path = "/api/signin")
    @ResponseBody
    public AuthResponse jwt(/*HttpServletRequest httpServletRequest*/@RequestBody AuthRequest authRequest) {

        //System.out.println(WebUtils.getCookie(httpServletRequest, "SESSION").getValue());
        //return userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication());
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getName(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(jwt);
    }

    /*@PostMapping(path = "/api/error")
    public String error() {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }*/

    @Transactional
    @PutMapping(path = "/pass", consumes ={"application/json"})
    public String changePassword(@RequestBody User user, Authentication authentication) {

        User userLogin = userService.getAuthUser(authentication);

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
    @PreAuthorize(value = "@userService.getAuthUser(authentication).username.equals(#username)")
    public void changeUser(@PathVariable String username, @RequestBody User user, Authentication authentication) {
        userService.updateUser(user.getLastName(), user.getFirstName(), username);
    }

    @GetMapping(path = "/about/{username}")
    public String getInfoAboutUser(Model model, @PathVariable String username) {
        User user = userService.reloadUser(username);
        model.addAttribute("user", user);
        return "user";
    }

    @PostMapping(path = "/checkyandex")
    @ResponseBody
    @Transactional
    public void checkyandex(String notification_type, String operation_id, Number amount, Number withdraw_amount,
                            String currency, String datetime, String sender, Boolean codepro, String label,
                            String sha1_hash, Boolean test_notification, Boolean unaccepted, String lastname,
                            String firstname, String fathersname, String email, String phone, String city,
                            String street, String building, String suite, String flat, String zip) throws NoSuchAlgorithmException {

        String mySha = notification_type + "&" + operation_id + "&" + amount + "&" + currency + "&" +
                datetime + "&" + sender + "&" + codepro + "&" + notification_secret + "&" + label;

        //System.out.println(mySha);

        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(mySha.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < result.length; i++) {
            sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
        }


        if (sb.toString().equals(sha1_hash)) {
            System.out.println("Пришло уведомление");
            System.out.println("notification_type = " + notification_type + ", operation_id = " + operation_id +
                    ", amount = " + amount + ", withdraw_amount = " + withdraw_amount + ", currency = " + currency +
                    ", datetime = " + datetime + ", sender = " + sender + ", codepro = " + codepro + ", label = " + label +
                    ", sha1_hash = " + sha1_hash + ", test_notification = " + test_notification + ", unaccepted = " + unaccepted + ", lastname = " + lastname + ", firstname = " + firstname + ", fathersname = " + fathersname + ", email = " + email + ", phone = " + phone + ", city = " + city + ", street = " + street + ", building = " + building + ", suite = " + suite + ", flat = " + flat + ", zip = " + zip);
            System.out.println();

            Order order = new Order();

            order.setAmount(String.valueOf(amount));
            order.setCoins((int) (Double.parseDouble(String.valueOf(withdraw_amount)) * 100.0));
            order.setOperation_id(operation_id);
            order.setOrder_id(Integer.valueOf(label));

            order = paymentServices.saveFinalOrder(order);
            userService.updateBalance(order.getCoins(), order.getUser());

        } else {
            System.out.println("Неправильный хэш");
        }

    }

    @GetMapping(path = "/getUserList")
    @ResponseBody
    public ArrayList<User> getUserList() {
        return userService.getUserList();
    }


    // UserService is required because this method is static, but UserService non-static

    @PostMapping(path = "/sendGeolocation")
    @ResponseBody
    public ArrayList<Geolocation> sendGeolocation(@RequestBody Geolocation geolocation) {
        geolocation.setUser(userService.getAuthUser(SecurityContextHolder.getContext().getAuthentication()));
        //geolocation.setId(geolocation.getUser().getUsername());
        userService.saveGeo(geolocation);
        ArrayList<Geolocation> arrayList = userService.getAllGeo();

        return arrayList;
    }
}
