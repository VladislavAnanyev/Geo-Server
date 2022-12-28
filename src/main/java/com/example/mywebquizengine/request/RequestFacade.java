package com.example.mywebquizengine.request;

public interface RequestFacade {
    void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent);
    AcceptRequestResult acceptRequest(Long requestId, Long userId);
    void rejectRequest(Long requestId, Long userId);
    GetSentFromUserRequestsResult getSentToUserRequests(Long userId);
    GetRequestsToUserResult getRequestsToUser(Long userId);
}
