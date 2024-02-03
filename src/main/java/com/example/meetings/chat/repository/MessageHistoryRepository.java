package com.example.meetings.chat.repository;

import com.example.meetings.chat.model.domain.MessageStatusHistory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface MessageHistoryRepository extends CrudRepository<MessageStatusHistory, Long> {
    /**
     * Получить количество непрочитанных пользователем сообщений в диалоге.
     * Определяется как разность всех сообщений в диалоге и прочитанных сообщений пользователем
     *
     * @param dialogId идентификатор диалога
     * @param userId   идентификатор пользователя
     * @return количество непрочитанных пользователем сообщений в диалоге
     */
    @Query(nativeQuery = true, value = """
            SELECT (
                SELECT COUNT(1)
                FROM messages
                WHERE messages.dialog_id =:dialogId AND messages.sender_user_id !=:userId
                ) - COUNT(1) AS unreadMessages
            FROM messages m LEFT OUTER JOIN message_status_history
            ON m.message_id = message_status_history.message_id
            WHERE message_status = 'READ' AND user_id =:userId AND dialog_id =:dialogId
            """)
    Integer findCountOfUnreadMessagesInDialogByUser(Long dialogId, Long userId);
}
