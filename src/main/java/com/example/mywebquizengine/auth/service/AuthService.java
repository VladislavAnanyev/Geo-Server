package com.example.mywebquizengine.auth.service;

import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.UserToken;
import com.example.mywebquizengine.auth.model.dto.input.AuthRequest;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.repository.TokenRepository;
import com.example.mywebquizengine.auth.security.model.AuthUserDetails;
import com.example.mywebquizengine.common.exception.*;
import com.example.mywebquizengine.common.model.Client;
import com.example.mywebquizengine.common.utils.AuthenticationUtil;
import com.example.mywebquizengine.common.utils.CodeUtil;
import com.example.mywebquizengine.common.utils.OauthUserMapper;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.repository.UserRepository;
import com.example.mywebquizengine.user.service.UserFactory;
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
        AuthUserDetails user = new AuthUserDetails();
        if (optionalUser.isPresent()) {
            user.setUserId(optionalUser.get().getUserId());
        } else {
            User saveUser = saveUser(registrationModel, RegistrationType.OAUTH2);
            user.setUserId(saveUser.getUserId());
        }

        AuthenticationUtil.setAuthentication(user, user.getAuthorities());
        return optionalUser.get();
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
     * При попытке смены пароля пользователю отправляется сообщение на почту с ссылкой,
     * содержащей уникальный код, который сохраняется в БД.
     * В дальнейшем, пользователь, перейдя по ссылке получит доступ для смены пароля
     *
     * @param username - имя пользователя, желающего сменить пароль
     * @param client   - платформа, с которой пользователь желает сменить пароль
     */
    @Transactional
    public User setChangePassword(String username, Client client) {
        User user = findUserByUsername(username);

        String code;
        if (client.equals(Client.MOBILE)) {
            /*
            В случае смены с телефона, пользователь будет вводить код вручную
            поэтому генерируется короткий, удобный код.
            */
            code = CodeUtil.generateShortCode();
        } else {
            /*
             В случае смены из браузера, пользователь будет переходить по ссылке из почты
             поэтому код может быть длинным
             */
            code = CodeUtil.generateLongCode();
        }
        user.setChangePasswordCode(code);
        return user;
    }

    /**
     * При смене пароля валидируется присланный код для смены пароля,
     * после чего меняется пароль, а код обнуляется
     *
     * @param username - имя пользователя, у которого меняется пароль
     * @param password - новый пароль
     */
    @Transactional
    public void changePassword(String username, String password) {
        userRepository.changePassword(passwordEncoder.encode(password), username, null);
    }

    /**
     * Проверка кода для смены пароля заключается в нахождении записи в БД
     * с указанным именем пользователя и коду для смены пароля
     *
     * @param username           - имя пользователя
     * @param changePasswordCode - код для смены пароля
     */
    public void verifyChangePasswordCode(String username, String changePasswordCode) {
        boolean exists = userRepository.existsByChangePasswordCodeAndUsername(
                changePasswordCode,
                username
        );

        if (!exists) {
            throw new WrongChangePasswordCodeException(
                    "exception.wrong.change.password.code",
                    GlobalErrorCode.ERROR_WRONG_CHANGE_PASSWORD_CODE
            );
        }
    }

    /**
     * Проверка существования пользователя с указанным username при входе
     *
     * @param username - проверяемое имя пользователя
     * @return true - существует
     */
    public boolean checkForExistUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }
        return true;
    }

    /**
     * Регистрация при помощи телефона
     *
     * @param registrationModel - модель для регистрации пользователя
     * @return - сохраненный пользователь
     */
    @Transactional
    public User saveUser(RegistrationModel registrationModel) {
        User user = userFactory.create(registrationModel, RegistrationType.PHONE);
        return userRepository.save(user);
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
        return code;
    }

    public UserToken updateRefreshToken(String oldRefreshToken) {
        Optional<UserToken> optionalUserToken = tokenRepository.findById(oldRefreshToken);
        if (optionalUserToken.isPresent()) {
            UserToken userToken = optionalUserToken.get();
            String newRefreshToken = UUID.randomUUID().toString();
            userToken.setRefreshToken(newRefreshToken);
            return tokenRepository.save(userToken);
        } else throw new EntityNotFoundException("Токен не найден");
    }
}
