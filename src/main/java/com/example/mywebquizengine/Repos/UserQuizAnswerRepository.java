package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.UserQuizAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserQuizAnswerRepository extends CrudRepository<UserQuizAnswer, Integer>,
        PagingAndSortingRepository<UserQuizAnswer,Integer> {

    /*@Query(value = "SELECT * FROM USER_ANSWERS u WHERE USER_USERNAME = :name AND STATUS = TRUE", nativeQuery = true)
    Page<UserQuizAnswer> getCompleteAnswersForUser(String name, Pageable paging);*/



    @Query(value = "SELECT COUNT(*) FROM USER_QUIZ_ANSWERS Q LEFT OUTER JOIN USER_TEST_ANSWERS T ON Q.USER_ANSWER_ID = T.USER_ANSWER_ID WHERE STATUS = TRUE AND TEST_ID = :id AND T.USER_ANSWER_ID = :answer", nativeQuery = true)
    Long getTrueAnswers(Integer id, Integer answer);

    @Query(value = "SELECT COUNT(*) FROM USER_QUIZ_ANSWERS Q LEFT OUTER JOIN USER_TEST_ANSWERS T ON Q.USER_ANSWER_ID = T.USER_ANSWER_ID WHERE TEST_ID = :id AND T.USER_ANSWER_ID = :answer", nativeQuery = true)
    Long getCountById(Integer id, Integer answer);

    /*@Query(value = "SELECT ANSWER_ID FROM USER_ANSWERS WHERE QUIZ_ID = :id", nativeQuery = true)
    List<Integer> getAnswerIdForQuiz(Integer id);*/

}

