package com.example.mywebquizengine.Controller;

import com.example.mywebquizengine.Model.Order;
import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Service.PaymentServices;
import com.example.mywebquizengine.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
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
        }
    }

    @PostMapping(path = "/update/userinfo/password", consumes ={"application/json"} )
    public void tryToChangePassWithAuth(Authentication authentication) {

        User user = getAuthUser(authentication, userService);
        userService.sendCodeForChangePassword(user);

    }

    @PostMapping(path = "/update/userinfo/pswrdwithoutauth", consumes ={"application/json"} )
    public void tryToChangePassWithoutAuth(@RequestBody User in) {

        User user = userService.reloadUser(in.getUsername());


        userService.sendCodeForChangePassword(user);

    }

    @GetMapping("/loginSuccess")
    public String getLoginInfo(Authentication authentication) {

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
    public String changePassword(@RequestBody User user, Authentication authentication) {

        User userLogin = getAuthUser(authentication, userService);

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
    @PreAuthorize(value = "@userController.getAuthUser(authentication,@userService).username.equals(#username)")
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

        System.out.println(mySha);

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
            order.setCoins((int) (Double.parseDouble(String.valueOf(amount)) * 100.0));
            order.setOperation_id(operation_id);
            order.setOrder_id(Integer.valueOf(label));

            paymentServices.saveFinalOrder(order);
            userService.updateBalance(order.getCoins());

        } else {
            System.out.println("Неправильный хэш");
        }

    }


    // UserService is required because this method is static, but UserService non-static
    public static User getAuthUser(Authentication authentication, UserService userService) {
        String name = "";



        if (authentication instanceof OAuth2AuthenticationToken) {

            if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("google")) {

                name = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes().get("email")
                        .toString().replace("@gmail.com", "");
            } else if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("github")) {
                name = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes().get("name")
                        .toString();
            }

        } else {
            User user = (User) authentication.getPrincipal();
            name = user.getUsername();
        }

        return userService.getUserProxy(name);
    }

}
