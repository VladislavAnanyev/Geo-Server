package com.example.mywebquizengine.service;

import com.example.mywebquizengine.model.common.Client;
import com.example.mywebquizengine.model.exception.*;
import com.example.mywebquizengine.model.userinfo.*;
import com.example.mywebquizengine.model.userinfo.domain.User;
import com.example.mywebquizengine.model.userinfo.dto.input.AuthRequest;
import com.example.mywebquizengine.model.userinfo.dto.output.AuthResult;
import com.example.mywebquizengine.repos.UserRepository;
import com.example.mywebquizengine.service.utils.*;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Сервис для регистрации/входа/смены пароля
 */
@Service
public class AuthService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtTokenUtil;
    private final RabbitAdmin rabbitAdmin;
    private final UserRepository userRepository;
    private final BusinessEmailSender businessEmailSender;
    private final UserFactory userFactory;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, JWTUtil jwtTokenUtil, RabbitAdmin rabbitAdmin,
                       UserRepository userRepository,
                       BusinessEmailSender businessEmailSender, UserFactory userFactory) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.rabbitAdmin = rabbitAdmin;
        this.userRepository = userRepository;
        this.businessEmailSender = businessEmailSender;
        this.userFactory = userFactory;
    }

    @Override
    public User loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);

        if (user.isPresent()) {
            return user.get();
        } else throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
    }

    /**
     * Регистрация в системе. Пользователь сохраняется в БД, ему отправляется приветственное сообщение на почту,
     * а также создается обмен в rabbitmq для отправления уведомлений
     *
     * @param registrationModel - модель для регистрации
     * @param type              - тип регистрации
     */
    public AuthResult signUp(RegistrationModel registrationModel, RegistrationType type) {
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getUsername());
        if (optionalUser.isPresent()) {
            throw new AlreadyRegisterException("exception.already.register", GlobalErrorCode.ERROR_USER_ALREADY_REGISTERED);
        }
        if (registrationModel.getUsername().contains(" ")) {
            throw new LogicException("exception.space.exist");
        }

        User user = userFactory.create(registrationModel, type);
        userRepository.save(user);
        businessEmailSender.sendWelcomeMessage(user);

        String exchangeName = RabbitUtil.getExchangeName(user.getUserId());
        rabbitAdmin.declareExchange(
                new FanoutExchange(
                        exchangeName,
                        true,
                        false
                )
        );

        return generateAuthResult(user, exchangeName);
    }

    /**
     * Если пользователь с username из токена уже был зарегистирован - получаем его и аутентифицируем
     * Иначе - регистрируем и аутентифицируем
     * todo - пофиксить дыру в безопасности, благодаря которой используя сторонний сервис войти в чужой аккаунт
     *
     * @param token - токен стороннего сервиса
     * @return - информация для входа в аккаунт
     */
    public AuthResult signInViaExternalServiceToken(Object token) {
        RegistrationModel registrationModel = OauthUserMapper.map(token);
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getUsername());
        User user;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            AuthResult authResult = signUp(registrationModel, RegistrationType.OAUTH2);
            user = userRepository.findById(authResult.getUserId()).get();
        }

        AuthenticationUtil.setAuthentication(user, user.getAuthorities());
        return generateAuthResult(user);
    }

    /**
     * Аутентификация происходит средствами spring
     *
     * @param authRequest - модель содержащая логин и пароль
     * @return -
     */
    public AuthResult signInViaApi(AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authRequest.getUsername(),
                            authRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw new AuthorizationException("exception.authorization", GlobalErrorCode.ERROR_WRONG_USERNAME_OR_PASSWORD);
        }

        User user = loadUserByUsername(authRequest.getUsername());
        return generateAuthResult(user);
    }

    /**
     * При попытке смены пароля пользователю отправляется сообщение на почту с ссылкой,
     * содержащей уникальный код, который сохраняется в БД.
     * В дальнейшем, пользователь, перейдя по ссылке получит доступ для смены пароля
     *
     * @param username - имя пользователя, желающего сменить пароль
     * @param client   - платформа, с которой пользователь желает сменить пароль
     */
    public void changePassword(String username, Client client) {
        User user = loadUserByUsername(username);

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
        userRepository.setChangePasswordCode(user.getUsername(), code);
        businessEmailSender.sendChangePasswordMessage(user, code, client);
    }

    /**
     * При смене пароля валидируется присланный код для смены пароля,
     * после чего меняется пароль, а код зануляется
     *
     * @param username - имя пользователя, у которого меняется пароль
     * @param code     - код для смены пароля
     * @param password - новый пароль
     */
    @Transactional
    public void updatePassword(String username, String code, String password) {
        verifyChangePasswordCode(username, code);
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
     * Проверяет существует ли пользователь с указанным username в системе
     * @param username - проверяемое имя пользователя
     * @return true - существует, false - не существует
     */
    public boolean checkForExistUser(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Генерация jwt токена, который клиент будет прикреплять с каждым запросом,
     * а также имени обмена, с которым клиент свяжет очередь в rabbitmq для получения уведомлений
     *
     * @param user - пользователя
     */
    private AuthResult generateAuthResult(User user) {
        String jwt = jwtTokenUtil.generateToken(user);
        return new AuthResult(user.getUserId(), jwt, RabbitUtil.getExchangeName(user.getUserId()));
    }

    /**
     * Генерация jwt токена, который клиент будет прикреплять с каждым запросом
     *
     * @param user - пользователя
     * @param exchangeName - Имя обмена
     */
    private AuthResult generateAuthResult(User user, String exchangeName) {
        String jwt = jwtTokenUtil.generateToken(user);
        return new AuthResult(user.getUserId(), jwt, exchangeName);
    }

    @Transactional
    public AuthPhoneResponse signUpViaPhone(String phone, String firstName, String lastName) {

        Optional<User> userByUsername = userRepository.findUserByUsername(phone);
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            String code = CodeUtil.generateShortCode();
            user.setPassword(passwordEncoder.encode(code));
            AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
            authPhoneResponse.setCode(code);
            return authPhoneResponse;
        } else {
            RegistrationModel registrationModel = new RegistrationModel();
            registrationModel.setUsername(phone);
            String code = CodeUtil.generateShortCode();
            registrationModel.setPassword(code);
            registrationModel.setEmail(phone);
            registrationModel.setLastName(lastName);
            registrationModel.setFirstName(firstName);
            registrationModel.setEmail(phone);
            User user = userFactory.create(registrationModel, RegistrationType.BASIC);
            userRepository.save(user);

            AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
            authPhoneResponse.setCode(code);
            return authPhoneResponse;
        }
    }

    @Transactional
    public AuthPhoneResponse signInViaPhone(String phone) {

        Optional<User> userByUsername = userRepository.findUserByUsername(phone);
        if (userByUsername.isPresent()) {
            User user = userByUsername.get();
            String code = CodeUtil.generateShortCode();
            user.setPassword(passwordEncoder.encode(code));
            AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
            authPhoneResponse.setCode(code);
            return authPhoneResponse;
        } else {
            RegistrationModel registrationModel = new RegistrationModel();
            registrationModel.setUsername(phone);
            String code = CodeUtil.generateShortCode();
            registrationModel.setPassword(code);
            registrationModel.setEmail(phone);
            registrationModel.setLastName(phone);
            registrationModel.setFirstName(phone);
            registrationModel.setEmail(phone);
            User user = userFactory.create(registrationModel, RegistrationType.BASIC);
            userRepository.save(user);

            AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
            authPhoneResponse.setCode(code);
            return authPhoneResponse;
        }
    }
}
