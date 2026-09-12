package com.example.meetings.user.service;

import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.common.exception.AlreadyRegisterException;
import com.example.meetings.util.Constants;
import com.example.meetings.common.exception.UserNotFoundException;
import com.example.meetings.user.model.ChangeUserRequest;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.*;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.example.meetings.util.Constants.DEFAULT_FIRST_NAME;
import static com.example.meetings.util.Constants.DEFAULT_LAST_NAME;


/**
 * Сервис управления пользователями: профиль, регистрация и загрузка.
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    /**
     * Возвращает данные пользователя для аутентификации.
     *
     * @param userId идентификатор пользователя
     * @return представление пользователя для авторизации
     */
    public AuthUserView getAuthUserInfo(Long userId) {
        return userRepository.findAllByUserId(userId);
    }

    /**
     * Возвращает профиль пользователя с фотографиями.
     *
     * @param userId идентификатор пользователя
     * @return профиль пользователя
     */
    @Transactional
    public ProfileView getUserProfileById(Long userId) {
        return userRepository.findUserByUserIdOrderByUsernameAscPhotosAsc(userId);
    }

    /**
     * Обновляет данные профиля пользователя.
     *
     * @param userId  идентификатор пользователя
     * @param request параметры изменения профиля
     */
    @Transactional
    public void changeUser(Long userId, ChangeUserRequest request) {
        User user = loadUserByUserId(userId);
        Optional.ofNullable(request.getFirstName()).ifPresent(user::setFirstName);
        Optional.ofNullable(request.getDescription()).ifPresent(user::setDescription);
        Optional.ofNullable(request.getGender()).ifPresent(user::setGender);
        Optional.ofNullable(request.getBirthDate()).ifPresent(user::setBirthDate);
    }

    /**
     * Загружает пользователя по идентификатору с проверкой существования.
     *
     * @param userId идентификатор пользователя
     * @return найденный пользователь
     */
    public User loadUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", Constants.ERROR_USER_NOT_FOUND);
        }

        return user.get();
    }

    /**
     * Загружает пользователя по идентификатору через JPA-прокси без немедленного обращения к БД.
     *
     * @param userId идентификатор пользователя
     * @return прокси-сущность пользователя
     */
    public User loadUserByUserIdProxy(Long userId) throws UsernameNotFoundException {
        return userRepository.getOne(userId);
    }

    /**
     * Регистрация в системе. Пользователь сохраняется в БД, ему отправляется приветственное сообщение на почту,
     * а также создается обмен в rabbitmq для отправления уведомлений
     *
     * @param registrationModel модель для регистрации
     * @return сохранённый пользователь
     */
    @Transactional
    public User saveUser(RegistrationModel registrationModel) {
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getPhoneNumber());
        if (optionalUser.isPresent()) {
            throw new AlreadyRegisterException(
                    "exception.already.register",
                    Constants.ERROR_USER_ALREADY_REGISTERED
            );
        }

        User user = userFactory.create(registrationModel);

        if (user.getFirstName() == null) {
            user.setFirstName(DEFAULT_FIRST_NAME + " " + DEFAULT_LAST_NAME);
        }

        return userRepository.save(user);
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
}
