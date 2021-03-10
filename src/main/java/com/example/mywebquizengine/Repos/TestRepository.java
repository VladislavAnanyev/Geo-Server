package com.example.mywebquizengine.Repos;

import com.example.mywebquizengine.Model.Quiz;
import com.example.mywebquizengine.Model.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends CrudRepository<Test, Integer>,
        PagingAndSortingRepository<Test, Integer> {

    @Query(value = "SELECT * FROM TEST u WHERE USER_USERNAME = :name", nativeQuery = true)
    Page<Test> getQuizForThis(String name, Pageable paging);
}
