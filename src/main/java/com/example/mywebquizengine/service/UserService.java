package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.*;
import com.example.mywebquizengine.model.projection.ProfileView;
import com.example.mywebquizengine.model.projection.UserCommonView;
import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.repos.PhotoRepository;
import com.example.mywebquizengine.repos.UserRepository;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
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
    private MailSender mailSender;
    @Value("${hostname}")
    private String hostname;

    @Autowired
    private PhotoRepository photoRepository;

    @Override
    public User loadUserByUsername(String username) throws ResponseStatusException {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            return user.get();
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public User loadUserByUsernameProxy(String username) throws UsernameNotFoundException {
        return userRepository.getOne(username);
    }

    private void saveUser(User user, RegistrationType type) {

        Optional<User> optionalUser = userRepository.findById(user.getUsername());
        if (type.equals(RegistrationType.OAUTH2)) {
            if (optionalUser.isEmpty()) {
                userRepository.save(user);
            }
        } else if (type.equals(RegistrationType.BASIC)) {
            if (optionalUser.isEmpty()) {
                userRepository.save(user);
            } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }

    public void updateUser(String lastName, String firstName, String username) {
        if (lastName != null && !lastName.trim().equals("") && firstName != null && !firstName.trim().equals("")) {
            userRepository.updateUserInfo(firstName, lastName, username);
        } else throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    public void sendCodeForChangePasswordFromPhone(String username) {

        User user = loadUserByUsername(username);

        Random random = new Random();
        int rage = 9999;
        int code = 1000 + random.nextInt(rage - 1000);

        userRepository.setChangePasswordCode(user.getUsername(), String.valueOf(code));

        try {
            mailSender.send(user.getEmail(), "Смена пароля в " + hostname,
                    """
                            Для смены пароля в """ + hostname +
                            """
                            введите в приложении код: """ + code +
                            """
                                    . 
                                    Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение
                                    """);

        } catch (Exception e) {
            System.out.println("Не отправлено");
        }
    }

    public void sendCodeForChangePassword(String username) {

        User user = loadUserByUsername(username);

        String code = UUID.randomUUID().toString();
        userRepository.setChangePasswordCode(user.getUsername(), code);

        try {
            mailSender.send(user.getEmail(), "Смена пароля в " + hostname, "Для смены пароля в " + hostname +
                    " перейдите по ссылке: https://" + hostname + "/updatepass/" + code + " Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение");

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @Transactional
    public void updatePassword(User user) {
        User savedUser = getUserViaChangePasswordCodeFromPhone(user.getUsername(), user.getChangePasswordCode());
        savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        savedUser.setChangePasswordCode(null);
    }

    private User getUserViaChangePasswordCodeFromPhone(String username, String changePasswordCode) {
        Optional<User> optionalUser = userRepository
                .findByChangePasswordCodeAndUsername(
                        changePasswordCode,
                        username
                );
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void activateAccount(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user != null) {
            user.setStatus(true);
            userRepository.activateAccount(user.getUsername());
        }
    }

    public User getUserViaChangePasswordCode(String changePasswordCode) {

        Optional<User> optionalUser = userRepository.findByChangePasswordCode(changePasswordCode);
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    public User castToUserFromOauth(OAuth2AuthenticationToken authentication) {

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
                user.setPhotos(Collections.singletonList((String) authentication.getPrincipal().getAttributes().get("picture")));

                user.setUsername(((String) authentication.getPrincipal().getAttributes()
                        .get("email")).replace("@gmail.com", ""));

            }


        } else if (authentication.getAuthorizedClientRegistrationId().equals("github")) {
            user.setUsername(authentication.getPrincipal().getAttributes().get("login").toString());
            user.setFirstName(authentication.getPrincipal().getAttributes().get("name").toString());
            user.setLastName(authentication.getPrincipal().getAttributes().get("name").toString());
            user.setPhotos(Collections.singletonList(authentication.getPrincipal().getAttributes().get("avatar_url").toString()));

            if (authentication.getPrincipal().getAttributes().get("email") != null) {
                user.setEmail(authentication.getPrincipal().getAttributes().get("email").toString());
            } else {
                user.setEmail("default@default.com");
            }
        }

        //doInitialize(user);

        return user;
    }

    public void doBasicUserInitializationBeforeSave(User user) {

        rabbitAdmin.declareExchange(new FanoutExchange(user.getUsername(), true, false));

        user.setEnabled(true);
        user.setBalance(0);
        user.grantAuthority(Role.ROLE_USER);
        user.setOnline("false");

    }

    public void updateBalance(Integer coins, String username) {
        User user = userRepository.findById(username).get();
        user.setBalance(user.getBalance() + coins);
    }

    public ArrayList<User> getUserList() {
        return (ArrayList<User>) userRepository.findAll();
    }

    public List<UserCommonView> findMyFriends(String username) {
        return userRepository.findUsersByFriendsUsername(username);
    }

    public UserCommonView getUserView(String username) {
        return userRepository.findByUsername(username);
    }

    public AuthResponse signInViaJwt(AuthRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken((UserDetails) authentication.getPrincipal());

        Queue queue = new Queue("", true, false, false);
        queue.addArgument("x-expires", 2419200000L);
        String queueName = rabbitAdmin.declareQueue(queue);
        Binding binding = new Binding(
                queueName,
                Binding.DestinationType.QUEUE,
                authRequest.getUsername(),
                "",
                null
        );
        rabbitAdmin.declareBinding(binding);

        return new AuthResponse(jwt, queueName);
    }

    public AuthResponse getJwtToken(User user) {
        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(loadUserByUsername(user.getUsername()));
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
            List<String> pictureUrl = Collections.singletonList((String) payload.get("picture"));
            String locale = (String) payload.get("locale");
            String familyName = (String) payload.get("family_name");
            String givenName = (String) payload.get("given_name");


            String username = email.replace("@gmail.com", "");

            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setFirstName(givenName);
            user.setLastName(familyName);
            user.setPhotos(pictureUrl);

            processCheckIn(user, RegistrationType.OAUTH2);

            User savedUser = loadUserByUsername(user.getUsername());

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            username, null, savedUser.getAuthorities()); // Проверить работу getAuthorities
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            String jwt = jwtTokenUtil.generateToken(savedUser);

            Queue queue = new Queue("", true, false, false);
            queue.addArgument("x-expires", 2419200000L);
            String queueName = rabbitAdmin.declareQueue(queue);
            Binding binding = new Binding(
                    queueName,
                    Binding.DestinationType.QUEUE,
                    user.getUsername(),
                    "",
                    null
            );
            rabbitAdmin.declareBinding(binding);

            return new AuthResponse(jwt, queueName);
            // Use or store profile information
            // ...

        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

        }
    }

    public UserView getAuthUser(String username) {
        if (userRepository.findAllByUsername(username) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return userRepository.findAllByUsername(username);
        }
    }

    @Transactional
    public void uploadPhoto(MultipartFile file, String username) {
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


                String photoUrl = "https://" + hostname + "/img/" + uuid + ".jpg";


                User user = loadUserByUsername(username);

                Photo photo = new Photo();
                photo.setUrl(photoUrl);
                photo.setPosition(user.getPhotos().size());
                user.addPhoto(photo);


            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }
        }
    }

    public void processCheckIn(User user, RegistrationType type) {
        if (user.getUsername().contains(" ")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            if (type.equals(RegistrationType.BASIC)) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setStatus(false);
                user.setPhotos(Collections.singletonList("https://" + hostname + "/img/default.jpg"));
                user.setActivationCode(UUID.randomUUID().toString());
                String mes = user.getFirstName() + " " + user.getLastName() + ", Добро пожаловать в WebQuizzes! "
                        + "Для активации аккаунта перейдите по ссылке: https://" + hostname + "/activate/" + user.getActivationCode()
                        + " Если вы не регистрировались на данном ресурсе, то проигнорируйте это сообщение";

                try {
                    mailSender.send(user.getEmail(), "Активация аккаунта в WebQuizzes", mes);
                } catch (Exception e) {
                    System.out.println("Отключено");
                }

            } else if (type.equals(RegistrationType.OAUTH2)) {
                user.setStatus(true);
            }
            doBasicUserInitializationBeforeSave(user);
            saveUser(user, type);
        }
    }

    public Boolean checkForExistUser(String username) {
        return userRepository.existsById(username);
    }

    @Transactional
    public ProfileView getUserProfileById(String username) {
        ProfileView profileView = userRepository.findUserByUsernameOrderByUsernameAscPhotosAsc(username);
        Collections.sort(profileView.getPhotos());
        return profileView;
    }

    @Transactional
    public void swapPhoto(Photo photo, String name) {

        Photo savedPhoto = photoRepository.findById(photo.getId()).get();
        List<Photo> photos = photoRepository.findByUser_Username(name);

        photos.remove(((int) savedPhoto.getPosition()));
        photos.add(photo.getPosition(), savedPhoto);

        for (int i = 0; i < photos.size(); i++) {
            photos.get(i).setPosition(i);
        }

    }

    public void getUserViaChangePasswordCodePhoneApi(String username, String code) {

        Optional<User> user = userRepository.findByChangePasswordCode(code);

        if (!(user.isPresent() && user.get().getUsername().equals(username))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
