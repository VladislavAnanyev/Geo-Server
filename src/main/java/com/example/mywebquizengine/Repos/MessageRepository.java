package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Chat.Message;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends CrudRepository<Message, Integer> {
    @Query(value = "SELECT * FROM MESSAGE WHERE (SENDER_USERNAME = :sent AND RECIPIENT_USERNAME = :received) OR (SENDER_USERNAME = :received AND RECIPIENT_USERNAME = :sent) ORDER BY TIMESTAMP", nativeQuery = true)
    List<Message> getMessagesByUsername(String sent, String received);


    @Query(value = "SELECT * FROM MESSAGE WHERE TIMESTAMP = :time", nativeQuery = true)
    Message getDialogsByTimestamp(String time);

    @Query(value = "SELECT MAX(TIMESTAMP) FROM MESSAGE WHERE RECIPIENT_USERNAME = :username OR SENDER_USERNAME = :username GROUP BY RECIPIENT_USERNAME, SENDER_USERNAME ORDER BY (MAX(TIMESTAMP)) DESC", nativeQuery = true)
    List<String> getTimeLastMessagesByUsername(String username);
}
