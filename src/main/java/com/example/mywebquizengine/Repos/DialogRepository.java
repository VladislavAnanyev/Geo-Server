package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Chat.Dialog;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface DialogRepository extends CrudRepository<Dialog, Long> {

    /*
    Если при группировке по идентификатору для выбранных пользователей результат группировки - 2
    (то есть было 2 записи с одинаковым id диалога)
    то диалог существует и возвращается его идентификатор
     */
    @Query(value = "SELECT DIALOG_ID\n" +
            "FROM USERS_DIALOGS\n" +
            "WHERE USER_ID = :firstUser or USER_ID = :secondUser\n" +
            "GROUP BY DIALOG_ID HAVING COUNT(DIALOG_ID) = 2", nativeQuery = true)
    Long findDialogByName(String firstUser, String secondUser);

}
