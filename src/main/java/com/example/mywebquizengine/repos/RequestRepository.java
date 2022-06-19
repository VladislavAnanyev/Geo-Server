package com.example.mywebquizengine.repos;

import com.example.mywebquizengine.model.request.dto.output.RequestView;

import com.example.mywebquizengine.model.request.domain.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends CrudRepository<Request, Long>, JpaRepository<Request, Long> {

    /*A and (B or C) <=> (A and B) or (A and C)
    * FindByStatusAnd(ToUsernameOrSenderUsername)*/
    ArrayList<RequestView> findByStatusAndToUserIdOrStatusAndSenderUserId(String status, @NotBlank Long toUserId, String status2, @NotBlank Long senderUserId);

    ArrayList<RequestView> findByMeetingMeetingIdAndSenderUserId(Long meetingId, Long userId);

    Optional<Request> findAllByMeetingMeetingIdAndStatusAndSenderUserId(Long meetingId, String status, Long userId);

    ArrayList<Request> findAllByMeetingMeetingId(Long meetingId);

    ArrayList<RequestView> findAllBySenderUserIdAndStatus(Long userId, String status);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE REQUESTS SET STATUS = :status WHERE REQUEST_ID = :id")
    void updateStatus(Long id, String status);

    List<Request> findBySenderUserIdAndToUserIdAndStatus(Long username, Long username1, String pending);
}
