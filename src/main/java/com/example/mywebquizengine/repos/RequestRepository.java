package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.projection.RequestView;

import com.example.mywebquizengine.model.request.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;

public interface RequestRepository extends CrudRepository<Request, Long>, JpaRepository<Request, Long> {

    RequestView findRequestById(Long id);

    ArrayList<RequestView> findAllByToUsernameAndStatus(String username, String status);

    /*A and (B or C) <=> (A and B) or (A and C)
    * FindByStatusAnd(ToUsernameOrSenderUsername)*/
    ArrayList<RequestView> findByStatusAndToUsernameOrStatusAndSenderUsername(String status, @NotBlank String to_username, String status2, @NotBlank String sender_username);

    ArrayList<Request> findAllByMeetingIdAndStatus(Long meetingId, String status);

    ArrayList<Request> findAllByMeetingId(Long meetingId);


    ArrayList<RequestView> findAllBySenderUsernameAndStatus(String username, String status);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE REQUESTS SET STATUS = :status WHERE ID = :id")
    void updateStatus(Long id, String status);
}
