package com.example.mywebquizengine.Controller.api;

import com.example.mywebquizengine.Model.AuthRequest;
import com.example.mywebquizengine.Model.AuthResponse;
import com.example.mywebquizengine.Model.GoogleToken;
import com.example.mywebquizengine.Model.Projection.DialogWithUsersView;

import com.example.mywebquizengine.Model.Projection.MessageForApiView;
import com.example.mywebquizengine.Model.Projection.UserForMessageView;
import com.example.mywebquizengine.Model.Projection.UserView;
import com.example.mywebquizengine.Model.User;
import com.example.mywebquizengine.Repos.DialogRepository;
import com.example.mywebquizengine.Repos.MessageRepository;
import com.example.mywebquizengine.Repos.UserRepository;
import com.example.mywebquizengine.Service.JWTUtil;
import com.example.mywebquizengine.Service.MessageService;
import com.example.mywebquizengine.Service.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.server.ResponseStatusException;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;

@RestController
public class AndroidController {

    private static final HttpTransport transport = new NetHttpTransport();
    private static final JsonFactory jsonFactory = new JacksonFactory();

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
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
    private MessageService messageService;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageRepository messageRepository;

    @GetMapping(path = "/api/messages")
    //@PreAuthorize(value = "@dialogRepository.findDialogByDialogId(#dialogId).users.contains(@userService.getAuthUser(authentication))")
    public DialogWithUsersView getMessages(@RequestParam Long dialogId) {

        if (dialogRepository.findDialogByDialogId(dialogId).getUsers().stream().anyMatch(o -> o.getUsername()
                .equals(userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication()).getUsername()))) {
            return dialogRepository.findDialogByDialogId(dialogId);
        } else throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        //return dialog.getMessages();
    }



    /*
        Вложенные поля проекции не инициализируются при пользовательском нативном запросе

        На данный момент работает следующим образом:
            Пользовательским запросом получается список диалогов, где диалог представлен только id сообщения,
            далее по всем этим id делается встроенный findById и выводятся диалоги

        В идеале необходимо решить проблему с инициализацией вложенных полей
     */
    @GetMapping(path = "/api/dialogs")
    @PreAuthorize(value = "@userService.getAuthUser(authentication).username.equals(#username)")
    public ArrayList<MessageForApiView> getDialogs(@RequestParam String username) {

        return messageService.getDialogsForApi(username);
    }

    @GetMapping(path = "/api/getDialogId")
    @ResponseBody
    public Long checkDialog(@RequestParam String username) {

        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //User user = userService.getAuthUserNoProxy(SecurityContextHolder.getContext().getAuthentication());

        return messageService.checkDialog(userService.loadUserByUsername(username));
    }


    @PostMapping(path = "/api/authuser")
    public UserView getApiAuthUser() {

        final String authorizationHeader = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest().getHeader("Authorization");
        String username = null;
        String jwt = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            //если подпись не совпадает с вычисленной, то SignatureException
            //если подпись некорректная (не парсится), то MalformedJwtException
            //если время подписи истекло, то ExpiredJwtException
            username = jwtTokenUtil.extractUsername(jwt);
        }

        return userRepository.findAllByUsername(username);
    }

    @GetMapping(path = "/api/findbyid")
    public UserForMessageView getUserById(@RequestParam String username) {
        return userRepository.findByUsername(username);
    }


    @PostMapping(path = "/api/signin")
    public AuthResponse jwt(@RequestBody AuthRequest authRequest) {

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

    /*@PostMapping(path = "/api/googleauth")
    public AuthResponse googleJwt(@RequestBody User user) {

        *//*try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }*//*

        userService.tryToSaveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.reloadUser(user.getUsername()));
        return new AuthResponse(jwt);
    }*/


    @PostMapping(path = "/api/signup")
    @ResponseBody
    public AuthResponse signup(@RequestBody User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userService.saveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
        return new AuthResponse(jwt);
    }


    /*@PostMapping(path = "/api/googleauth")
    public AuthResponse googleJwt(@RequestBody Authentication authentication) {



        *//*try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }*//*
        User user = userService.castToUser((OAuth2AuthenticationToken) authentication);

        userService.tryToSaveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.loadUserByUsername(user.getUsername()));
        return new AuthResponse(jwt);
    }*/

    @PostMapping(path = "/api/googleauth")
    public AuthResponse googleJwt(@RequestBody GoogleToken token) throws GeneralSecurityException, IOException {

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
                // Specify the CLIENT_ID of the app that accesses the backend:
                .setAudience(Collections.singletonList(CLIENT_ID))
                // Or, if multiple clients access the backend:
                //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
                .build();

        GoogleIdToken idToken = verifier.verify(token.getIdTokenString());
        if (idToken != null) {
            Payload payload = idToken.getPayload();

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
            //user.setAvatar("default");

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
            //System.out.println("Error");
        /*try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            System.out.println(authentication);
        } catch (BadCredentialsException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Имя или пароль неправильны", e);
        }*/
/*        User user = userService.castToUser((OAuth2AuthenticationToken) authentication);

        userService.tryToSaveUser(user);

        // при создании токена в него кладется username как Subject claim и список authorities как кастомный claim
        String jwt = jwtTokenUtil.generateToken(userService.loadUserByUsername(user.getUsername()));*/
        //return new AuthResponse(jwt);

    }


}
}