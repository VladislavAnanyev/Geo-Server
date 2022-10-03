package com.example.mywebquizengine.chat.repository;

import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MessageRepository extends
        CrudRepository<Message, Long>,
        JpaRepository<Message, Long>,
        PagingAndSortingRepository<Message, Long> {

    Page<Message> findAllByDialog_DialogIdAndStatusNot(Long dialogId, MessageStatus status, Pageable paging);

    @Query(value = """
            SELECT MESSAGES.MESSAGE_ID, content, DIALOGS.dialog_id as dialogId,
                   MESSAGES.SENDER_USER_ID as userId, U.USERNAME as username,
                   email, first_name as firstName, ONLINE as online, last_name as lastName,
                   MESSAGES.status as status, image, name, timestamp as timestamp, AVATAR
            FROM DIALOGS
                     LEFT OUTER JOIN MESSAGES
                                     on DIALOGS.last_message_id = MESSAGES.MESSAGE_ID
                     LEFT OUTER JOIN USERS U
                                     on U.USER_ID = MESSAGES.SENDER_USER_ID
            WHERE MESSAGES.DIALOG_ID IN (
                SELECT USERS_DIALOGS.DIALOG_ID
                FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :userId
                )
              and MESSAGES.DIALOG_ID IN (
                SELECT * FROM (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D on D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                JOIN USERS_FRIENDS
                     ON USERS_FRIENDS.FRIEND_ID = USERS_DIALOGS.USER_ID
                WHERE USERS_FRIENDS.USER_ID = :userId and name is null)
                UNION
                (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D on D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                WHERE USER_ID = :userId and name is not null)
                )
            ORDER BY MESSAGES.TIMESTAMP DESC;
            """, nativeQuery = true)
    List<LastDialog> getLastDialogs(Long userId);

    List<Message> findAllByDialog_DialogIdAndStatusNotAndSenderUserIdNot(Long dialogId, MessageStatus deleted, Long userId);

    @Query(value = """
            SELECT MESSAGES.MESSAGE_ID, content, DIALOGS.dialog_id as dialogId,
                   MESSAGES.SENDER_USER_ID as userId, U.USERNAME as username, activation_code,
                   balance, email, first_name as firstName,
                   ONLINE as online, last_name as lastName,
                   MESSAGES.status as status, image, name, timestamp as timestamp,
                   AVATAR
            FROM MESSAGES
                     LEFT OUTER JOIN USERS U
                                     on U.USER_ID = MESSAGES.SENDER_USER_ID
                     LEFT OUTER JOIN DIALOGS
                                     on DIALOGS.DIALOG_ID = MESSAGES.DIALOG_ID
            WHERE MESSAGES.TIMESTAMP IN (
                SELECT MAX(MESSAGES.TIMESTAMP)
                FROM MESSAGES
                WHERE MESSAGES.STATUS != 'DELETED'
                GROUP BY MESSAGES.DIALOG_ID
                )
              and MESSAGES.DIALOG_ID IN (
                SELECT USERS_DIALOGS.DIALOG_ID
                FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :userId
                )
              and MESSAGES.DIALOG_ID IN (
                SELECT * FROM (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D on D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                JOIN USERS_FRIENDS
                     ON USERS_FRIENDS.FRIEND_ID = USERS_DIALOGS.USER_ID
                WHERE USERS_FRIENDS.USER_ID = :userId and name is null)
                UNION
                (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D on D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                WHERE USER_ID = :userId and name is not null)
                )
            ORDER BY MESSAGES.TIMESTAMP DESC;
            """, nativeQuery = true)
    List<LastDialog> getLastDialogs2(Long userId);
}
