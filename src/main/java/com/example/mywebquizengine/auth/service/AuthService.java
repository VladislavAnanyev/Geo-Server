package com.example.mywebquizengine.auth.service;

import com.example.mywebquizengine.auth.model.RegistrationType;
import com.example.mywebquizengine.auth.model.dto.input.RegistrationModel;
import com.example.mywebquizengine.auth.model.dto.output.AuthPhoneResponse;
import com.example.mywebquizengine.auth.model.dto.output.AuthResult;
import com.example.mywebquizengine.auth.model.dto.output.UserExistDto;
import com.example.mywebquizengine.common.common.Client;
import com.example.mywebquizengine.common.exception.*;
import com.example.mywebquizengine.user.service.UserFactory;
import com.example.mywebquizengine.user.model.domain.Device;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.auth.model.dto.input.AuthRequest;
import com.example.mywebquizengine.user.repository.DeviceRepository;
import com.example.mywebquizengine.user.repository.UserRepository;
import com.example.mywebquizengine.common.SmsSender;
import com.example.mywebquizengine.common.utils.*;
import com.example.mywebquizengine.user.service.BusinessEmailSender;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Calendar;
import java.util.GregorianCalendar;
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
    private final SmsSender smsSender;
    private final UserFactory userFactory;
    private final DeviceRepository deviceRepository;
    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthService(PasswordEncoder passwordEncoder, JWTUtil jwtTokenUtil, RabbitAdmin rabbitAdmin,
                       UserRepository userRepository, BusinessEmailSender businessEmailSender,
                       SmsSender smsSender, UserFactory userFactory, DeviceRepository deviceRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.rabbitAdmin = rabbitAdmin;
        this.userRepository = userRepository;
        this.businessEmailSender = businessEmailSender;
        this.smsSender = smsSender;
        this.userFactory = userFactory;
        this.deviceRepository = deviceRepository;
    }

    @Override
    public User loadUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findUserByUsername(username);

        if (user.isPresent()) {
            return user.get();
        } else throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
    }

    public User findUserByUsername(String username) throws UserNotFoundException {
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

        String exchangeName = createExchange(user.getUserId());
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
     * @return - модель содержащая имя обмена, jwt и идентификатор пользователя
     */
    public AuthResult signInViaApi(AuthRequest authRequest) {
        User user = findUserByUsername(authRequest.getUsername());
        Calendar now = new GregorianCalendar();
        if (now.after(user.getSignInViaPhoneCodeExpiration())) {
            throw new CodeExpiredException("exception.code.expired", GlobalErrorCode.ERROR_CODE_EXPIRED);
        }

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
     *
     * @param username - проверяемое имя пользователя
     * @return true - существует, false - не существует
     */
    public UserExistDto checkForExistUser(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        UserExistDto userExistDto = new UserExistDto();
        userExistDto.setExist(true);
        return userExistDto;
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
     * @param user         - пользователя
     * @param exchangeName - Имя обмена
     */
    private AuthResult generateAuthResult(User user, String exchangeName) {
        String jwt = jwtTokenUtil.generateToken(user);
        return new AuthResult(user.getUserId(), jwt, exchangeName);
    }

    /**
     * Регистрация при помощи телефона
     *
     * @param registrationModel - модель для регистрации пользователя
     * @return - код для входа в систему (в качестве тестирования)
     */
    @Transactional
    public AuthPhoneResponse signUpViaPhone(RegistrationModel registrationModel) {
        String code = CodeUtil.generateShortCode();
        registrationModel.setPassword(code);
        User user = userFactory.create(registrationModel, RegistrationType.PHONE);

        userRepository.save(user);
        createExchange(user.getUserId());

        // сохранение токена apple устройства для отправки уведомлений
        // если указанный токен ещё не принадлежит пользователю
        if (registrationModel.getAppleToken() != null) {
            boolean exist = user.getDevices().stream().anyMatch(
                    device -> registrationModel.getAppleToken().equals(device.getDeviceToken())
            );

            if (!exist) {
                Device device = new Device();
                device.setDeviceToken(registrationModel.getAppleToken());
                device.setUser(user);
                deviceRepository.save(device);
            }
        }

        smsSender.sendCodeToPhone(code, registrationModel.getUsername());

        AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
        authPhoneResponse.setCode(code);
        return authPhoneResponse;
    }

    /**
     * Отправить код для входа в систему с телефона
     * Используется когда, например, пользователь не успел ввести код за отведенное время при входе
     * Или для входа, после разлогина
     *
     * @param phone - номер телефона
     * @return - код для входа в систему
     */
    @Transactional
    public AuthPhoneResponse generateCodeForSignInViaPhone(String phone) {
        Optional<User> optionalUser = userRepository.findUserByUsername(phone);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        User user = optionalUser.get();
        String code = CodeUtil.generateShortCode();
        user.setPassword(passwordEncoder.encode(code));

        smsSender.sendCodeToPhone(code, phone);

        AuthPhoneResponse authPhoneResponse = new AuthPhoneResponse();
        authPhoneResponse.setCode(code);
        return authPhoneResponse;
    }

    private String createExchange(Long userId) {
        String exchangeName = RabbitUtil.getExchangeName(userId);
        rabbitAdmin.declareExchange(
                new FanoutExchange(
                        exchangeName,
                        true,
                        false
                )
        );
        return exchangeName;
    }
}
