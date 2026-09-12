package com.example.meetings.user.service;

import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.model.dto.UserCommonView;
import com.example.meetings.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис операций со списком друзей пользователя.
 */
@Service
@RequiredArgsConstructor
public class FriendService {

    private final UserRepository userRepository;
    private final UserService userService;

    /**
     * Добавляет двух пользователей в друзья (двусторонняя связь).
     *
     * @param firstUserId  идентификатор первого пользователя
     * @param secondUserId идентификатор второго пользователя
     */
    @Transactional
    public void makeFriends(Long firstUserId, Long secondUserId) {
        User firstUser = userService.loadUserByUserId(firstUserId);
        User secondUser = userService.loadUserByUserId(secondUserId);
        firstUser.addFriend(secondUser);
    }

    /**
     * Проверяет, являются ли пользователи друзьями.
     *
     * @param fromUserId идентификатор первого пользователя
     * @param toUserId   идентификатор второго пользователя
     * @return {@code true}, если пользователи в списке друзей друг у друга
     */
    public boolean isFriends(Long fromUserId, Long toUserId) {
        User firstUser = userService.loadUserByUserId(fromUserId);
        return firstUser.getFriends().stream()
                .map(User::getUserId)
                .collect(Collectors.toSet())
                .contains(toUserId);
    }

    /**
     * Возвращает список друзей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return проекции друзей пользователя
     */
    @Transactional(readOnly = true)
    public List<UserCommonView> findMyFriends(Long userId) {
        return userRepository.findUsersByFriendsUserId(userId);
    }

    /**
     * Удаляет пользователя из списка друзей (двусторонняя связь).
     *
     * @param friendUserId идентификатор удаляемого друга
     * @param authUserId   идентификатор текущего пользователя
     */
    @Transactional
    public void deleteFriend(Long friendUserId, Long authUserId) {
        User user = userService.loadUserByUserId(authUserId);
        User friend = userService.loadUserByUserId(friendUserId);
        user.removeFriend(friend);
    }
}
