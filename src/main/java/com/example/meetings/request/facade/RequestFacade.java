package com.example.meetings.request.facade;

import com.example.meetings.request.model.dto.output.AcceptRequestResult;
import com.example.meetings.request.model.dto.output.GetRequestsToUserResult;
import com.example.meetings.request.model.dto.output.GetSentFromUserRequestsResult;

public interface RequestFacade {
    void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent);
    AcceptRequestResult acceptRequest(Long requestId, Long userId);
    void rejectRequest(Long requestId, Long userId);
    GetSentFromUserRequestsResult getSentToUserRequests(Long userId);
    GetRequestsToUserResult getRequestsToUser(Long userId);
}
