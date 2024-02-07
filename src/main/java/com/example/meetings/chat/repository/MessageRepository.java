package com.example.meetings.chat.repository;

import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.chat.model.domain.MessageStatus;
import com.example.meetings.chat.model.dto.output.LastDialog;
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
            SELECT MESSAGES.MESSAGE_ID AS messageId, content, DIALOGS.dialog_id AS dialogId,
                   MESSAGES.SENDER_USER_ID AS userId, U.USERNAME AS username,
                   first_name AS firstName, ONLINE AS online, last_name AS lastName,
                   MESSAGES.status AS status, timestamp AS timestamp
            FROM DIALOGS
                     LEFT OUTER JOIN MESSAGES
                                     ON DIALOGS.last_message_id = MESSAGES.MESSAGE_ID
                     LEFT OUTER JOIN USERS U
                                     ON U.USER_ID = MESSAGES.SENDER_USER_ID
            WHERE MESSAGES.DIALOG_ID IN (
                SELECT USERS_DIALOGS.DIALOG_ID
                FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :userId
                )
              AND MESSAGES.DIALOG_ID IN (
                SELECT * FROM (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D ON D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                JOIN USERS_FRIENDS
                     ON USERS_FRIENDS.FRIEND_ID = USERS_DIALOGS.USER_ID
                WHERE USERS_FRIENDS.USER_ID = :userId AND name IS NULL) AS friends
                UNION
                (SELECT D.DIALOG_ID FROM USERS_DIALOGS JOIN DIALOGS D ON D.DIALOG_ID = USERS_DIALOGS.DIALOG_ID
                WHERE USER_ID = :userId AND name IS NOT NULL)
                )
            ORDER BY MESSAGES.TIMESTAMP DESC;
            """, nativeQuery = true)
    List<LastDialog> getLastDialogs(Long userId);

}
