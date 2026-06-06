package com.example.meetings.chat.repository;

import com.example.meetings.chat.model.domain.Dialog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface DialogRepository extends CrudRepository<Dialog, Long>, JpaRepository<Dialog, Long>,
        PagingAndSortingRepository<Dialog, Long> {

    /*
    Если при группировке по идентификатору для выбранных пользователей результат группировки - 2
    (то есть было 2 записи с одинаковым id диалога)
    то диалог существует и возвращается его идентификатор
     */
    @Query(value = """
            SELECT a.DIALOG_ID, COUNT(a.DIALOG_ID)
            FROM USERS_DIALOGS a join public.dialogs d on a.dialog_id = d.dialog_id
            WHERE (USER_ID = :firstUser or USER_ID = :secondUser) AND
                (SELECT COUNT(DIALOG_ID) FROM USERS_DIALOGS b where b.DIALOG_ID = a.DIALOG_ID) = 2 AND type = 'PRIVATE'
            GROUP BY a.DIALOG_ID HAVING COUNT(a.DIALOG_ID) = 2""", nativeQuery = true)
    Long findDialogBetweenUsers(Long firstUser, Long secondUser);

}
