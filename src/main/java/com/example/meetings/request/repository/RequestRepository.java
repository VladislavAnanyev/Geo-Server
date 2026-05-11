package com.example.meetings.request.repository;

import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.model.dto.output.RequestView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends CrudRepository<Request, Long>, JpaRepository<Request, Long> {

    /*A and (B or C) <=> (A and B) or (A and C)
     * FindByStatusAnd(ToUsernameOrSenderUsername)*/
    List<RequestView> findByStatusAndToUserIdOrStatusAndSenderUserId(RequestStatus status, @NotBlank Long toUserId, RequestStatus status2, @NotBlank Long senderUserId);

    List<RequestView> findAllBySenderUserIdAndStatus(Long userId, RequestStatus status);

    boolean existsBySenderUserIdAndToUserIdAndStatus(Long username, Long username1, RequestStatus pending);

    Optional<Request> findBySenderUserIdAndToUserIdAndStatus(Long senderUserId, Long toUserId, RequestStatus status);

    Optional<Request> findFirstBySenderUserIdAndToUserIdAndStatusOrderByRequestIdDesc(Long senderUserId, Long toUserId, RequestStatus status);
}
