package com.example.meetings.chat.mapper;


import com.example.meetings.chat.model.DialogDTO;
import com.example.meetings.chat.model.UserDto;
import com.example.meetings.chat.model.domain.*;
import com.example.meetings.user.model.domain.User;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.meetings.chat.mapper.DtoMapper.getMessages;


public class DialogDTOMapper {
    /**
     * @param dialog информация о диалоге
     * @param userId пользователь запрашивающий диалог
     * @return подготовленная к выводу информация о диалоге
     */
    public static DialogDTO map(Dialog dialog, List<Message> messageList, Long userId) {
        return new DialogDTO()
                .setType(dialog.getType().toString())
                .setDialogId(dialog.getDialogId())
                .setImage(getImage(dialog, userId))
                .setName(getName(dialog, userId))
                .setUsers(getUsers(dialog))
                .setMessages(getMessages(messageList));
    }

    private static Set<UserDto> getUsers(Dialog dialog) {
        return dialog.getUsers().stream().map(user -> new UserDto()
                .setAvatar(user.getMainPhoto().getUrl())
                .setFirstName(user.getFirstName())
                .setOnline(user.isOnline())
                .setLogin(user.getUsername())
                .setUserId(user.getUserId())).collect(Collectors.toSet());
    }

    /**
     * Получить отображаемое изображение диалога
     *
     * @param dialog информация о диалоге
     * @param userId идентификатор пользователя, который получает диалог
     * @return отображаемое изображение
     */
    public static String getImage(Dialog dialog, Long userId) {
        if (dialog.getType().equals(DialogType.GROUP)) {
            return dialog.getImage();
        }

        if (dialog.getType().equals(DialogType.PRIVATE)) {
            Set<User> userSet = new HashSet<>(dialog.getUsers());
            return userSet
                    .stream()
                    .filter(user -> !user.getUserId().equals(userId))
                    .findFirst().orElseThrow()
                    .getMainPhoto().getUrl();
        }

        throw new UnsupportedOperationException("Не поддерживаемый тип диалога");
    }

    /**
     * Получить отображаемое имя диалога
     *
     * @param dialog информация о диалоге
     * @param userId идентификатор пользователя, который получает диалог
     * @return отображаемое имя
     */
    public static String getName(Dialog dialog, Long userId) {
        if (dialog.getType().equals(DialogType.GROUP)) {
            return dialog.getName();
        } else if (dialog.getType().equals(DialogType.PRIVATE)) {
            Set<User> userSet = new HashSet<>(dialog.getUsers());
            User companion = userSet
                    .stream()
                    .filter(user -> !user.getUserId().equals(userId))
                    .findFirst()
                    .orElseThrow();

            return companion.getFirstName();
        }

        throw new UnsupportedOperationException("Не поддерживаемый тип диалога");
    }
}
