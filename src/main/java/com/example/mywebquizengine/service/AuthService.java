package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.exception.AuthorizationException;
import com.example.mywebquizengine.model.exception.LogicException;
import com.example.mywebquizengine.model.exception.UserNotFoundException;
import com.example.mywebquizengine.model.projection.UserView;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.repos.UserRepository;
import com.example.mywebquizengine.service.utils.CodeUtil;
import com.example.mywebquizengine.service.utils.OauthUserMapper;
import com.example.mywebquizengine.service.utils.RabbitUtil;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private MailSender mailSender;
    @Value("${hostname}")
    private String hostname;
    @Autowired
    private JWTUtil jwtTokenUtil;
    @Autowired
    private RabbitAdmin rabbitAdmin;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(username);

        if (user.isPresent()) {
            return user.get();
        } else throw new EntityNotFoundException("Пользователь не найден");
    }

    public User processCheckIn(User user, RegistrationType type) {
        if (user.getUsername().contains(" ")) {
            throw new IllegalArgumentException("\"username\" не должен содержать пробелов");
        } else {
            if (type.equals(RegistrationType.BASIC)) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                user.setStatus(false);
                user.setPhotos(Collections.singletonList(hostname + "/img/default.jpg"));
                user.setActivationCode(UUID.randomUUID().toString());
                String mes = user.getFirstName() + " " + user.getLastName() + ", Добро пожаловать в WebQuizzes! "
                        + "Для активации аккаунта перейдите по ссылке: " + hostname + "/activate/" + user.getActivationCode()
                        + " Если вы не регистрировались на данном ресурсе, то проигнорируйте это сообщение";

                mailSender.send(user.getEmail(), "Активация аккаунта в WebQuizzes", mes);

            } else if (type.equals(RegistrationType.OAUTH2)) {
                user.setStatus(true);
            }
            rabbitAdmin.declareExchange(
                    new FanoutExchange(RabbitUtil.getExchangeName(user.getUsername()),
                            true,
                            false)
            );

            user.setEnabled(true);
            user.setBalance(0);
            user.grantAuthority(Role.ROLE_USER);
            user.setOnline("false");
            return saveUser(user, type);
        }
    }

    @Transactional
    public void updatePassword(User user) {
        User savedUser = getUserViaChangePasswordCodeFromPhone(user.getUsername(), user.getChangePasswordCode());
        savedUser.setPassword(passwordEncoder.encode(user.getPassword()));
        savedUser.setChangePasswordCode(null);
    }

    public AuthResult signinViaGoogleToken(GoogleToken token) {
        User user = processCheckIn(
                OauthUserMapper.castToUserFromOauth(token),
                RegistrationType.OAUTH2
        );
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                new UsernamePasswordAuthenticationToken(
                        user, null, user.getAuthorities()); // Проверить работу getAuthorities
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

        String exchangeName = RabbitUtil.getExchangeName(user.getUsername());
        rabbitAdmin.declareExchange(new FanoutExchange(exchangeName, true, false));

        return new AuthResult(jwtTokenUtil.generateToken(user), exchangeName);
    }

    public void sendCodeForChangePasswordFromPhone(String username) {

        User user = loadUserByUsername(username);
        String code = CodeUtil.generate();
        userRepository.setChangePasswordCode(user.getUsername(), code);

        mailSender.send(user.getEmail(), "Смена пароля в " + hostname,
                """
                        Для смены пароля в """ + hostname +
                        """
                                введите в приложении код: """ + code +
                        """
                                . 
                                 Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение
                                """);
    }

    public AuthResult signInViaApi(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AuthorizationException("Имя или пароль неправильны");
        }

        String jwt = jwtTokenUtil.generateToken(
                loadUserByUsername(authRequest.getUsername())
        );
        String exchangeName = RabbitUtil.getExchangeName(authRequest.getUsername());
        return new AuthResult(jwt, exchangeName);
    }

    public AuthResult getJwtToken(User user) {
        String jwt = jwtTokenUtil.generateToken(loadUserByUsername(user.getUsername()));
        String exchangeName = RabbitUtil.getExchangeName(user.getUsername());
        return new AuthResult(jwt, exchangeName);
    }

    public UserView getAuthUser(String username) {
        if (userRepository.findAllByUsername(username) == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            return userRepository.findAllByUsername(username);
        }
    }

    public void sendCodeForChangePassword(String username) {

        User user = loadUserByUsername(username);
        String code = UUID.randomUUID().toString();
        userRepository.setChangePasswordCode(user.getUsername(), code);
        mailSender.send(user.getEmail(), "Смена пароля в " + hostname, "Для смены пароля в " + hostname +
                " перейдите по ссылке: " + hostname + "/updatepass/" + code + " Если вы не меняли пароль на данном ресурсе, то проигнорируйте сообщение");

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

    private User saveUser(User user, RegistrationType type) {
        Optional<User> optionalUser = userRepository.findById(user.getUsername());
        if (type.equals(RegistrationType.OAUTH2)) {
            if (optionalUser.isEmpty()) {
                return userRepository.save(user);
            } else return optionalUser.get();
        } else if (type.equals(RegistrationType.BASIC)) {
            if (optionalUser.isEmpty()) {
                return userRepository.save(user);
            } else throw new LogicException("Пользователь с таким \"username\" уже существует");
        } else throw new IllegalArgumentException();
    }

    public void getUserViaChangePasswordCodePhoneApi(String username, String code) {
        Optional<User> user = userRepository.findByChangePasswordCode(code);
        if (!(user.isPresent() && user.get().getUsername().equals(username))) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
