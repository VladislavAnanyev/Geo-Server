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

    List<RequestView> findByMeetingMeetingIdAndSenderUserId(Long meetingId, Long userId);

    Optional<Request> findAllByMeetingMeetingIdAndStatusAndSenderUserId(Long meetingId, RequestStatus status, Long userId);

    List<Request> findAllByMeetingMeetingId(Long meetingId);

    List<RequestView> findAllBySenderUserIdAndStatus(Long userId, RequestStatus status);

    boolean existBySenderUserIdAndToUserIdAndStatus(Long username, Long username1, RequestStatus pending);
}
