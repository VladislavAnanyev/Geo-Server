package com.example.mywebquizengine.request;

import com.example.mywebquizengine.request.model.dto.output.RequestView;

import java.util.ArrayList;
import java.util.List;

public interface RequestFacade {
    void sendRequest(Long meetingId, Long fromUserId, Long toUserId, String messageContent);
    Long acceptRequest(Long requestId, Long userId);
    void rejectRequest(Long requestId, Long userId);
    List<RequestView> getSentRequests(Long userId);
    ArrayList<RequestView> getMyRequests(Long userId);
}
