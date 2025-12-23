package com.example.meetings.user.service;

import com.example.meetings.auth.model.dto.input.RegistrationModel;
import com.example.meetings.common.exception.AlreadyRegisterException;
import com.example.meetings.common.exception.GlobalErrorCode;
import com.example.meetings.common.exception.UserNotFoundException;
import com.example.meetings.user.model.ChangeUserRequest;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.*;
import com.example.meetings.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

import static com.example.meetings.util.Constants.DEFAULT_FIRST_NAME;
import static com.example.meetings.util.Constants.DEFAULT_LAST_NAME;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserFactory userFactory;

    public AuthUserView getAuthUserInfo(Long userId) {
        return userRepository.findAllByUserId(userId);
    }

    public List<UserCommonView> findMyFriends(Long userId) {
        return userRepository.findUsersByFriendsUserId(userId);
    }

    @Transactional
    public ProfileView getUserProfileById(Long userId) {
        return userRepository.findUserByUserIdOrderByUsernameAscPhotosAsc(userId);
    }

    @Transactional
    public void deleteFriend(Long userId, Long authUserId) {
        User user = loadUserByUserId(authUserId);
        User friend = loadUserByUserId(userId);
        user.removeFriend(friend);
    }

    @Transactional
    public void changeUser(Long userId, ChangeUserRequest request) {
        User user = loadUserByUserId(userId);
        user.setDescription(request.getDescription());
        user.setFirstName(request.getFirstName());
    }

    public User loadUserByUserId(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException("exception.user.not.found", GlobalErrorCode.ERROR_USER_NOT_FOUND);
        }

        return user.get();
    }

    public User loadUserByUserIdProxy(Long userId) throws UsernameNotFoundException {
        return userRepository.getOne(userId);
    }

    /**
     * Регистрация в системе. Пользователь сохраняется в БД, ему отправляется приветственное сообщение на почту,
     * а также создается обмен в rabbitmq для отправления уведомлений
     *
     * @param registrationModel - модель для регистрации
     */
    @Transactional
    public User saveUser(RegistrationModel registrationModel) {
        Optional<User> optionalUser = userRepository.findUserByUsername(registrationModel.getPhoneNumber());
        if (optionalUser.isPresent()) {
            throw new AlreadyRegisterException(
                    "exception.already.register",
                    GlobalErrorCode.ERROR_USER_ALREADY_REGISTERED
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
