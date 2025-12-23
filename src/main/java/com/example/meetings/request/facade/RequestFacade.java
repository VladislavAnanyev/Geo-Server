package com.example.meetings.request.facade;

import com.example.meetings.request.model.dto.output.*;

public interface RequestFacade {
    void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent);

    AcceptRequestResult acceptRequest(Long requestId, Long userId);

    void doNotShowInRecommendation(Long meetingId, Long userId);

    void rejectRequest(Long requestId, Long userId);

    GetSentFromUserRequestsResult getSentToUserRequests(Long userId);

    GetRequestsToUserResult getRequestsToUser(Long userId);
}
