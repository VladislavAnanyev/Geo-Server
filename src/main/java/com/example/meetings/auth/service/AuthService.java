package com.example.meetings.auth.service;

import com.example.meetings.auth.model.RegistrationType;
import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.auth.repository.TokenRepository;
import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.exception.*;
import com.example.meetings.common.utils.*;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import com.example.meetings.user.service.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

import static java.time.LocalDateTime.now;

/**
 * Сервис для регистрации/входа/смены пароля
 */
@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserFactory userFactory;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenRepository tokenRepository;

    @Override
    public AuthUserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findUserByUsername(username);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(
                    "exception.user.not.found",
                    GlobalErrorCode.ERROR_USER_NOT_FOUND
            );
        }

        User user = optionalUser.get();
        return new AuthUserDetails()
                .setUserId(user.getUserId())
                .setPassword(user.getPassword());
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        return user.get();
    }

    /**
     * Регистрация в системе. Пользователь сохраняется в БД, ему отправляется приветственное сообщение на почту,
     * а также создается обмен в rabbitmq для отправления уведомлений
     *
     * @param registrationModel - модель для регистрации
     * @param type              - тип регистрации
     */
    @Transactional
    public User saveUser(RegistrationModel registrationModel, RegistrationType type) {
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getUsername());
        if (optionalUser.isPresent()) {
            throw new AlreadyRegisterException(
                    "exception.already.register",
                    GlobalErrorCode.ERROR_USER_ALREADY_REGISTERED
            );
        }

        if (registrationModel.getUsername().contains(" ")) {
            throw new LogicException("exception.space.exist");
        }

        User user = userFactory.create(registrationModel, type);

        return userRepository.save(user);
    }

    /**
     * Если пользователь с username из токена уже был зарегистирован - получаем его и аутентифицируем
     * Иначе - регистрируем и аутентифицируем
     * todo - пофиксить дыру в безопасности, благодаря которой используя сторонний сервис войти в чужой аккаунт
     *
     * @param token - токен стороннего сервиса
     * @return - информация для входа в аккаунт
     */
    public User signInViaExternalServiceToken(Object token) {
        RegistrationModel registrationModel = OauthUserMapper.map(token);
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getUsername());
        User user = optionalUser.orElseGet(() -> saveUser(registrationModel, RegistrationType.OAUTH2));

        AuthUserDetails authUserDetails = new AuthUserDetails();
        authUserDetails.setUserId(user.getUserId());
        AuthenticationUtil.setAuthentication(authUserDetails, authUserDetails.getAuthorities());

        return user;
    }

    /**
     * Аутентификация происходит средствами spring
     *
     * @param authRequest - модель содержащая логин и пароль
     * @return - модель содержащая имя обмена, jwt и идентификатор пользователя
     */
    public User authenticate(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AuthorizationException(
                    "exception.authorization",
                    GlobalErrorCode.ERROR_WRONG_USERNAME_OR_PASSWORD
            );
        }

        return findUserByUsername(authRequest.getUsername());
    }

    /**
     * Проверка существования пользователя с указанным username при входе
     *
     * @param username - проверяемое имя пользователя
     * @return true - существует
     */
    public boolean isUserExist(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Установить код для входа в систему с телефона
     * Используется, когда, например, пользователь не успел ввести код за отведенное время при входе
     * Или для входа, после выхода из аккаунта
     *
     * @param phone - номер телефона
     * @return - код для входа в систему
     */
    @Transactional
    public String setOneTimePasswordCode(String phone) {
        Optional<User> optionalUser = userRepository.findUserByUsername(phone);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        User user = optionalUser.get();
        String code = CodeUtil.generateShortCode();
        user.setPassword(passwordEncoder.encode(code));
        user.setSignInViaPhoneCodeExpiration(now().plusMinutes(3));

        return code;
    }

    public UserToken updateRefreshToken(String oldRefreshToken) {
        Optional<UserToken> optionalUserToken = tokenRepository.findById(oldRefreshToken);
        if (optionalUserToken.isEmpty()) {
            throw new EntityNotFoundException("Токен не найден");
        }

        UserToken userToken = optionalUserToken.get();
        userToken.setRefreshToken(UUID.randomUUID().toString());

        return tokenRepository.save(userToken);
    }
}
