package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Test.UserTestAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface UserTestAnswerRepository extends CrudRepository<UserTestAnswer, Integer>,
        PagingAndSortingRepository<UserTestAnswer,Integer> {


    @Query(value = "SELECT * FROM USER_TEST_ANSWERS u WHERE TEST_ID = :id", nativeQuery = true)
    Page<UserTestAnswer> getAnswersOnMyQuiz(int id, Pageable paging);

    @Query(value = "SELECT USER_ANSWER_ID FROM USER_TEST_ANSWERS u WHERE TEST_ID = :id", nativeQuery = true)
    List<Integer> getUserAnswersById(int id);


}
