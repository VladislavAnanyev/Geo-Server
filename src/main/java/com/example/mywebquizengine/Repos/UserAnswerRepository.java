package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.UserAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAnswerRepository extends CrudRepository<UserAnswer, Integer>,
        PagingAndSortingRepository<UserAnswer,Integer> {

    @Query(value = "SELECT * FROM USER_ANSWERS u WHERE USER_USERNAME = :name AND STATUS = TRUE", nativeQuery = true)
    Page<UserAnswer> getCompleteAnswersForUser(String name, Pageable paging);

    @Query(value = "SELECT * FROM USER_ANSWERS u WHERE QUIZ_ID = :id", nativeQuery = true)
    Page<UserAnswer> getAnswersOnMyQuiz(int id, Pageable paging);

    @Query(value = "SELECT COUNT(*) FROM USER_ANSWERS WHERE STATUS = TRUE AND QUIZ_ID = :id", nativeQuery = true)
    Long getTrueAnswers(Integer id);

    @Query(value = "SELECT COUNT(*) FROM USER_ANSWERS WHERE QUIZ_ID = :id", nativeQuery = true)
    Long getCountById(Integer id);

    @Query(value = "SELECT ANSWER_ID FROM USER_ANSWERS WHERE QUIZ_ID = :id", nativeQuery = true)
    List<Integer> getAnswerIdForQuiz(Integer id);

}

