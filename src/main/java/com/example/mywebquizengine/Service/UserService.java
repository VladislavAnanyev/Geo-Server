package com.example.mywebquizengine.Service;

import com.example.mywebquizengine.Model.Geo.Geolocation;
import com.example.mywebquizengine.Model.Order;
import com.example.mywebquizengine.Model.Projection.GeolocationView;
import com.example.mywebquizengine.Model.Projection.UserCommonView;
import com.example.mywebquizengine.Model.Projection.UserView;
import com.example.mywebquizengine.Model.Role;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Model.UserInfo.AuthRequest;
import com.example.mywebquizengine.Model.UserInfo.AuthResponse;
import com.example.mywebquizengine.Model.UserInfo.GoogleToken;
import com.example.mywebquizengine.Repos.GeolocationRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.util.*;


@Service
public class UserService implements UserDetailsService {

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    @Value("${androidGoogleClientId}")
    private String CLIENT_ID;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTUtil jwtTokenUtil;

    @Autowired
    private RabbitAdmin rabbitAdmin;

    @Autowired
    private UserService userService;

    @Autowired
    private MailSender mailSender;

    @Value("${hostname}")
    private String hostname;

    @Value("${notification-secret}")
    String notification_secret;

    @Autowired
    private PaymentServices paymentServices;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Override
    public User /*UserDetails*/ loadUserByUsername(String username) throws UsernameNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findById(username);



        if (user.isPresent()) {
            return user.get();
        } /*else if (authentication != null){
            return castToUser((OAuth2AuthenticationToken) authentication);
        }*/ else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }


    public User /*UserDetails*/ loadUserByUsernameProxy(String username) throws UsernameNotFoundException {
        return userRepository.getOne(username);
    }

    public void setAvatar(String avatar, User user) {
        userRepository.setAvatar(avatar, user.getUsername());
    }

    public void saveUser(User user){
        if (userRepository.findById(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            user.setActivationCode(UUID.randomUUID().toString());
            user.setChangePasswordCode(UUID.randomUUID().toString());
            doInitialize(user);
            userRepository.save(user);
            loadUserByUsername(user.getUsername());

            String mes = user.getFirstName() + " " + user.getLastName() + ", Добро пожаловать в WebQuizzes! "
                    + "Для активации аккаунта перейдите по ссылке: https://" + hostname + "/activate/" + user.getActivationCode()
                    + " Если вы не регистрировались на данном ресурсе, то проигнорируйте это сообщение";

            try {
                mailSender.send(user.getEmail(),"Активация аккаунта в WebQuizzes", mes);
            } catch (Exception e) {
                System.out.println("Отключено");
            }

        }
    }

    public void saveGeo(Geolocation geolocation) {


            /*if (geolocationRepository.existsById(geolocation.getUser().getUsername())) {

                geolocationRepository.updateGeo(geolocation.getLat(), geolocation.getLng(), geolocation.getUser().getUsername());
            } else {

                geolocationRepository.save(geolocation);
            }*/


            geolocationRepository.save(geolocation);


        //geolocationRepository.insertGeo(geolocation.getLat(), geolocation.getLng(), geolocation.getUser().getUsername());
    }


    public ArrayList<GeolocationView> getAllGeo(String username) {
        return (ArrayList<GeolocationView>) geolocationRepository.getAll(username);
    }




    public void updateUser(String lastName, String firstName, String username) {
        userRepository.updateUserInfo(firstName, lastName, username);
    }

    public void sendCodeForChangePassword(User user) {
        String mes = user.getChangePasswordCode();

        if (mes == null) {
            mes = UUID.randomUUID().toString();
            userRepository.setChangePasswordCode(user.getUsername(), mes);

        }

        try {
            mailSender.send(user.getEmail(),"Смена пароля в WebQuizzes", "Для смены пароля в WebQuizzes" +
                    " перейдите по ссылке: https://" + hostname + "/updatepass/" + mes + " Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение");

        } catch (Exception e) {
            System.out.println("Не отправлено");
        }
    }


    @Transactional
    public void updatePassword(User user, String changePasswordCode) {


        User savedUser = userService.getUserViaChangePasswordCode(changePasswordCode);
        user.setUsername(savedUser.getUsername());


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setChangePasswordCode(UUID.randomUUID().toString());
        userRepository.changePassword(user.getPassword(), user.getUsername(), user.getChangePasswordCode());
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUsername());
        }

    }

    public User getUserViaChangePasswordCode(String changePasswordCode) {

        if (userRepository.findByChangePasswordCode(changePasswordCode).isPresent()) {
            return userRepository.findByChangePasswordCode(changePasswordCode).get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public void tryToSaveUser(User user) {

        if (userRepository.findById(user.getUsername()).isEmpty()) {
            doInitialize(user);
            userRepository.save(user);
        }
        if (userRepository.findById(user.getUsername()).get().getAvatar().contains("default")) {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication instanceof OAuth2AuthenticationToken) {
                if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("google")) {
                    user.setAvatar(((DefaultOidcUser) authentication.getPrincipal()).getPicture());
                    userRepository.setAvatar(user.getAvatar(), user.getUsername());

                }  else if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("github")) {
                    user.setAvatar(((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes().get("avatar_url").toString());
                    userRepository.setAvatar(user.getAvatar(), user.getUsername());
                }
            }



        }


    }

    public User castToUser(OAuth2AuthenticationToken authentication) {

        User user = new User();

        if (authentication.getAuthorizedClientRegistrationId().equals("google")) {

            String username = ((String) authentication.getPrincipal().getAttributes()
                    .get("email")).replace("@gmail.com", "");

            if (userRepository.findById(username).isPresent()) {
                user = userRepository.findById(username).get();
            } else {

            user.setEmail((String) authentication.getPrincipal().getAttributes().get("email"));
            user.setFirstName((String) authentication.getPrincipal().getAttributes().get("given_name"));
            user.setLastName((String) authentication.getPrincipal().getAttributes().get("family_name"));
            user.setAvatar((String) authentication.getPrincipal().getAttributes().get("picture"));

            user.setUsername(((String) authentication.getPrincipal().getAttributes()
                    .get("email")).replace("@gmail.com", ""));

        }


    } else if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
            user.setUsername(authentication.getPrincipal().getAttributes().get("login").toString());
            user.setFirstName(authentication.getPrincipal().getAttributes().get("name").toString());
            user.setLastName(authentication.getPrincipal().getAttributes().get("name").toString());
            user.setAvatar(authentication.getPrincipal().getAttributes().get("avatar_url").toString());

            if (authentication.getPrincipal().getAttributes().get("email") != null) {
                user.setEmail(authentication.getPrincipal().getAttributes().get("email").toString());
            } else {
                user.setEmail("default@default.com");
            }
        }

        user.setStatus(true);
        //doInitialize(user);

        return user;
    }

    public void doInitialize(User user) {

        Queue queue = new Queue(user.getUsername(), true, false, false);

        Binding binding = new Binding(user.getUsername(), Binding.DestinationType.QUEUE,
                "message-exchange", user.getUsername(), null);

        rabbitAdmin.declareQueue(queue);
        rabbitAdmin.declareBinding(binding);

        if (user.getAvatar() == null) {
            user.setAvatar("https://" + hostname + "/img/default.jpg");
        }

        user.setEnabled(true);
        user.setStatus(false);
        user.setBalance(0);

        user.grantAuthority(Role.ROLE_USER);
        user.setChangePasswordCode(UUID.randomUUID().toString());
        //user.setActivationCode(UUID.randomUUID().toString());
    }


    public void updateBalance(Integer coins, User user) {
        User user2 = userRepository.findById(user.getUsername()).get();
        user2.setBalance(user2.getBalance() + coins);
    }

    /*public User getAuthUser(Authentication authentication) {
        String name = getAuthUserCommon(authentication);
        //String name;

        return getUserProxy(name);
    }


    public User getAuthUserNoProxy(Authentication authentication) {
        String name = getAuthUserCommon(authentication);

        return loadUserByUsername(name);
    }


    private String getAuthUserCommon(Authentication authentication) {
        String name = "";


        if (authentication instanceof OAuth2AuthenticationToken) {

            if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("google")) {

                name = ((DefaultOidcUser) authentication.getPrincipal()).getAttributes().get("email")
                        .toString().replace("@gmail.com", "");
            } else if (((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId().equals("github")) {
                name = ((DefaultOAuth2User) authentication.getPrincipal()).getAttributes().get("name")
                        .toString();
            }

        }
        else if (authentication instanceof UsernamePasswordAuthenticationToken) {
            if (authentication.getCredentials() != null) {
                name = (authentication).getPrincipal().toString();

            } else {
                User user = (User) authentication.getPrincipal();
                name = user.getUsername();
            }
        } else if (authentication instanceof RememberMeAuthenticationToken) {
            name = ((User) authentication.getPrincipal()).getUsername();
        }
        return name;
    }*/


    public void processPayment(String notification_type, String operation_id, Number amount, Number withdraw_amount, String currency, String datetime, String sender, Boolean codepro, String label, String sha1_hash, Boolean test_notification, Boolean unaccepted, String lastname, String firstname, String fathersname, String email, String phone, String city, String street, String building, String suite, String flat, String zip) throws NoSuchAlgorithmException {
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
            updateBalance(order.getCoins(), order.getUser());

        } else {
            System.out.println("Неправильный хэш");
        }
    }




    public ArrayList<User> getUserList() {
        return (ArrayList<User>) userRepository.findAll();
    }

    public List<UserCommonView> findMyFriends(Principal principal) {
        return userRepository.findUsersByFriendsUsername(principal.getName());
    }

    public UserCommonView getUserView(String username) {
        return userRepository.findByUsername(username);
    }

    public AuthResponse signInViaJwt(AuthRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            //System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());
        return new AuthResponse(jwt);
    }

    public AuthResponse signUpViaJwt(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
        return new AuthResponse(jwt);
    }

    public AuthResponse signinViaGoogleToken(GoogleToken token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(token.getIdTokenString());
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Print user identifier
            String userId = payload.getSubject();
            System.out.println("User ID: " + userId);

            // Get profile information from payload
            String email = payload.getEmail();
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            String name = (String) payload.get("name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");



            String username = email.replace("@gmail.com", "");

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setAvatar(pictureUrl);

            userService.tryToSaveUser(user);

            User savedUser = userService.loadUserByUsername(user.getUsername());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username, null, savedUser.getAuthorities()); // Проверить работу getAuthorities
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            String jwt = jwtTokenUtil.generateToken(savedUser);
            return new AuthResponse(jwt);
            // Use or store profile information
            // ...

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
    }

    public UserView getAuthUser(Principal principal) {
        if (userRepository.findAllByUsername(principal.getName()) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return userRepository.findAllByUsername(principal.getName());
        }
    }

    public void uploadPhoto(MultipartFile file, Principal principal) {
        if (!file.isEmpty()) {
            try {
                String uuid = UUID.randomUUID().toString();
                uuid = uuid.substring(0, 8);
                byte[] bytes = file.getBytes();

                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File("img/" +
                                uuid + ".jpg")));
                stream.write(bytes);
                stream.close();

                User user = userService.loadUserByUsernameProxy(principal.getName());

                userService.setAvatar("https://" + hostname + "/img/" + uuid + ".jpg", user);


            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void processCheckIn(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setEnabled(false);
        user.setAvatar("https://" + hostname + "/img/default.jpg");
        user.grantAuthority(Role.ROLE_USER);
        userService.saveUser(user);
    }
}
