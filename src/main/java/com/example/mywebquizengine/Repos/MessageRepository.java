package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Chat.Message;
import com.example.mywebquizengine.Model.Projection.DialogWithUsersView;
import com.example.mywebquizengine.Model.Projection.MessageForApiView;
import com.example.mywebquizengine.Model.Projection.MessageForStompView;
import com.example.mywebquizengine.Model.Projection.MessageView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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

    @Query(value = "SELECT *\n" +
            "FROM MESSAGES \n" +

            "WHERE MESSAGES.TIMESTAMP IN (SELECT MAX(MESSAGES.TIMESTAMP)\n" +
            "FROM MESSAGES GROUP BY MESSAGES.DIALOG_ID) and MESSAGES.DIALOG_ID IN (SELECT USERS_DIALOGS.DIALOG_ID\n" +
            "FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :username) ORDER BY MESSAGES.TIMESTAMP DESC", nativeQuery = true)
    List<Message> getDialogs(String username);

    /*@Query(value = "SELECT id\n" +
            "FROM MESSAGES \n" +
            "    LEFT OUTER JOIN USERS U\n" +
            "        on U.USERNAME = MESSAGES.SENDER_USERNAME \n" +
            "    LEFT OUTER JOIN DIALOGS \n" +
            "        on DIALOGS.DIALOG_ID = MESSAGES.DIALOG_ID\n" +
            "WHERE MESSAGES.TIMESTAMP IN (SELECT MAX(MESSAGES.TIMESTAMP)\n" +
            "FROM MESSAGES GROUP BY MESSAGES.DIALOG_ID) and MESSAGES.DIALOG_ID IN (SELECT USERS_DIALOGS.DIALOG_ID\n" +
            "FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :username) ORDER BY MESSAGES.TIMESTAMP DESC", nativeQuery = true)
    List<MessageForStompView> getDialogsForApi(String username);*/

    @Query(value = "SELECT id\n" +
            "FROM MESSAGES \n" +
            "    LEFT OUTER JOIN USERS U\n" +
            "        on U.USERNAME = MESSAGES.SENDER_USERNAME \n" +
            "    LEFT OUTER JOIN DIALOGS \n" +
            "        on DIALOGS.DIALOG_ID = MESSAGES.DIALOG_ID\n" +
            "WHERE MESSAGES.TIMESTAMP IN (SELECT MAX(MESSAGES.TIMESTAMP)\n" +
            "FROM MESSAGES GROUP BY MESSAGES.DIALOG_ID) and MESSAGES.DIALOG_ID IN (SELECT USERS_DIALOGS.DIALOG_ID\n" +
            "FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :username) ORDER BY MESSAGES.TIMESTAMP DESC", nativeQuery = true)
    List<Integer> getDialogsIdForApi(String username);

    /*@Query(value = "SELECT id, content, timestamp, DIALOGS.dialog_id as dialogId, MESSAGES.SENDER_USERNAME as users_username, activation_code, avatar, balance, change_password_code, email, first_name, last_name, password, MESSAGES.status, image, name\n" +
            "FROM MESSAGES \n" +
            "    LEFT OUTER JOIN USERS U\n" +
            "        on U.USERNAME = MESSAGES.SENDER_USERNAME \n" +
            "    LEFT OUTER JOIN DIALOGS \n" +
            "        on DIALOGS.DIALOG_ID = MESSAGES.DIALOG_ID\n" +
            "WHERE MESSAGES.TIMESTAMP IN (SELECT MAX(MESSAGES.TIMESTAMP)\n" +
            "FROM MESSAGES GROUP BY MESSAGES.DIALOG_ID) and MESSAGES.DIALOG_ID IN (SELECT USERS_DIALOGS.DIALOG_ID\n" +
            "FROM USERS_DIALOGS WHERE USERS_DIALOGS.USER_ID = :username) ORDER BY MESSAGES.TIMESTAMP DESC", nativeQuery = true)
    List<DialogWithUsersView> getDialogsForApi(String username);*/

    /*@Query(value = "SELECT * FROM MESSAGES WHERE ID = :id", nativeQuery = true)
    MessageViewForStomp findMessageById(Integer id);*/

    List<MessageForStompView> findMessagesById(List<Integer> integers);

    ArrayList<MessageForStompView> findAllById(ArrayList<Integer> integers);

    MessageForApiView findMessageById(Integer id);

    //MessageForStompView findMessageById(Integer id);

    Message getById(Integer id);




}
