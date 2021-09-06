package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Chat.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
    @Query(value = "SELECT * FROM MESSAGES WHERE (SENDER_USERNAME = :sent AND RECIPIENT_USERNAME = :received) OR (SENDER_USERNAME = :received AND RECIPIENT_USERNAME = :sent) ORDER BY TIMESTAMP", nativeQuery = true)
    List<Message> getMessagesByUsername(String sent, String received);

    @Query(value = "SELECT * FROM MESSAGES WHERE GROUP_ID = :group ORDER BY TIMESTAMP", nativeQuery = true)
    List<Message> getMessagesByGroup(String group);


    @Query(value = "SELECT * FROM MESSAGES WHERE TIMESTAMP = :time", nativeQuery = true)
    Message getDialogsByTimestamp(String time);

    @Query(value = "SELECT MAX(TIMESTAMP) FROM MESSAGES WHERE RECIPIENT_USERNAME = :username OR SENDER_USERNAME = :username GROUP BY RECIPIENT_USERNAME, SENDER_USERNAME ORDER BY (MAX(TIMESTAMP)) DESC", nativeQuery = true)
    List<String> getTimeLastMessagesByUsername(String username);

    @Query(value = "SELECT DIALOG_ID FROM USERS_DIALOGS WHERE USER_ID = :username", nativeQuery = true)
    List<Long> getMyDialogsId(String username);

    @Query(value = "SELECT * FROM MESSAGES WHERE TIMESTAMP IN (SELECT MAX(TIMESTAMP)\n" +
            "                                          FROM MESSAGES\n" +
            "                                          GROUP BY DIALOG_ID)\n" +
            "                         and DIALOG_ID IN ( SELECT DIALOG_ID\n" +
            "                                            FROM USERS_DIALOGS\n" +
            "                                            WHERE USER_ID = :username) ORDER BY TIMESTAMP DESC ", nativeQuery = true)
    List<Message> getDialogs(String username);
}
