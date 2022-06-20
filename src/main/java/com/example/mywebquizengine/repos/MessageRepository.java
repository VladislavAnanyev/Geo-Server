package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.chat.domain.Message;
import com.example.mywebquizengine.model.chat.domain.MessageStatus;

import com.example.mywebquizengine.model.chat.dto.output.LastDialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Long>, PagingAndSortingRepository<Message, Long> {

    Page<Message> findAllByDialog_DialogIdAndStatusNot(Long dialogId, MessageStatus status, Pageable paging);

    @Query(value = """
            SELECT MESSAGES.MESSAGE_ID, content, DIALOGS.dialog_id as dialogId,
                   MESSAGES.SENDER_USER_ID as userId, U.USERNAME as username, activation_code,
                   balance, change_password_code, email, first_name as firstName,
                   ONLINE as online, last_name as lastName, password,
                   MESSAGES.status as status, image, name, timestamp as timestamp,
                   up.URL as avatar
            FROM MESSAGES
                     LEFT OUTER JOIN USERS U
                                     on U.USER_ID = MESSAGES.SENDER_USER_ID
                     LEFT OUTER JOIN DIALOGS
                                     on DIALOGS.DIALOG_ID = MESSAGES.DIALOG_ID
                     LEFT OUTER JOIN (SELECT * FROM USERS_PHOTOS WHERE POSITION = 0) AS UP
                                     on U.USER_ID = UP.USER_ID
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
    List<LastDialog> getLastDialogs(Long userId);

}
