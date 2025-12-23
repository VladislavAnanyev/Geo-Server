package com.example.meetings.auth.service;

import com.example.meetings.auth.model.UserToken;
import com.example.meetings.auth.model.dto.input.AuthRequest;
import com.example.meetings.auth.repository.TokenRepository;
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

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
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
     * Аутентификация происходит средствами spring
     *
     * @param authRequest - модель содержащая логин и пароль
     * @return - модель содержащая имя обмена, jwt и идентификатор пользователя
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
     * Установить код для входа в систему с телефона
     * Используется, когда, например, пользователь не успел ввести код за отведенное время при входе
     * Или для входа, после выхода из аккаунта
     *
     * @param phone - номер телефона
     */
    @Transactional
    public void setOneTimePasswordCode(String phone, String code) {
        User user = findUserByUsername(phone);
        user.setPassword(passwordEncoder.encode(code));
        user.setSignInViaPhoneCodeExpiration(now().plusMinutes(3));
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
