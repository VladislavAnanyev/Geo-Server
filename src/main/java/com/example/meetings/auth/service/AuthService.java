package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.security.model.AuthUserDetails;
import com.example.meetings.common.exception.AuthorizationException;
import com.example.meetings.common.exception.CodeExpiredException;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.UserNotFoundException;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

import static java.time.LocalDateTime.now;

/**
 * Бизнес-логика аутентификации: загрузка пользователя, вход и обновление refresh-токена.
 */
@Service
public class AuthService implements UserDetailsService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private TokenService tokenService;

    /**
     * Загружает данные пользователя для Spring Security по имени (номеру телефона).
     *
     * @param username логин пользователя
     * @return учётные данные для аутентификации
     */
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

    /**
     * Находит пользователя по имени (номеру телефона).
     *
     * @param username логин пользователя
     * @return найденный пользователь
     */
    public User findUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        return user.get();
    }

    /**
     * Аутентифицирует пользователя по логину и паролю через Spring Security.
     *
     * @param authRequest логин и пароль
     * @return аутентифицированный пользователь
     */
    public User authenticate(AuthRequest authRequest) {
        try {
            User user = findUserByUsername(authRequest.getUsername());
            if (LocalDateTime.now().isAfter(user.getSignInViaPhoneCodeExpiration())) {
                throw new CodeExpiredException("exception.code.expired", GlobalErrorCode.ERROR_CODE_EXPIRED);
            }

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
     * Сохраняет одноразовый код входа по телефону и срок его действия.
     * <p>Используется при повторной отправке кода или после выхода из аккаунта.</p>
     *
     * @param phone номер телефона
     * @param code  одноразовый код
     */
    @Transactional
    public void setOneTimePasswordCode(String phone, String code) {
        User user = findUserByUsername(phone);
        user.setPassword(passwordEncoder.encode(code));
        user.setSignInViaPhoneCodeExpiration(now().plusMinutes(3));
    }

    /**
     * Обновляет refresh-токен: выдаёт новый идентификатор для существующей записи.
     *
     * @param oldRefreshToken текущий refresh-токен
     * @return обновлённая запись токена
     */
    public UserToken updateRefreshToken(String oldRefreshToken) {
        return tokenService.updateRefreshToken(oldRefreshToken);
    }
}
