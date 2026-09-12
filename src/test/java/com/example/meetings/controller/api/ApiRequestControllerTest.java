package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.request.facade.RequestFacade;
import com.example.meetings.user.repository.UserRepository;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.repository.RequestRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.REQUESTS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiRequestControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RequestFacade requestFacade;

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testSendRequest() throws Exception {
        String json = """
                {
                    "toUserId": 1004,
                    "messageContent": "Привет"
                }
                """;
        long pendingBefore = requestRepository.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .count();

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(pendingBefore + 1, requestRepository.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .count());
        assertTrue(requestRepository.findBySenderUserIdAndToUserIdAndStatus(1003L, 1004L, RequestStatus.PENDING)
                .isPresent());

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testSendRequestWithoutMessage() throws Exception {
        String json = """
                {
                    "toUserId": 1004
                }
                """;
        long countBefore = requestRepository.count();

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(countBefore + 1, requestRepository.count());
        assertTrue(requestRepository.findBySenderUserIdAndToUserIdAndStatus(1003L, 1004L, RequestStatus.PENDING)
                .isPresent());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testRejectRequest() throws Exception {
        mockMvc.perform(post("/api/v1/request/800/reject").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(RequestStatus.REJECTED, requestRepository.findById(800L).orElseThrow().getStatus());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testSendRequestWhenAlreadyFriends() throws Exception {
        String json = """
                {
                    "toUserId": 1002,
                    "messageContent": "Привет"
                }
                """;
        long countBefore = requestRepository.count();

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        assertEquals(countBefore, requestRepository.count());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetRequests() throws Exception {
        mockMvc.perform(get("/api/v1/requests").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.requests", hasSize(1)))
                .andExpect(jsonPath("$.result.requests[0].requestId").value(800))
                .andExpect(jsonPath("$.result.requests[0].status").value("PENDING"))
                .andExpect(jsonPath("$.result.requests[0].sender.userId").value(1001));
    }

    @Test
    @WithUserDetails(value = "user4", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testAcceptRequest() throws Exception {
        assertTrue(userRepository.findUsersByFriendsUserId(1003L).stream()
                .noneMatch(u -> u.getUserId().equals(1004L)));

        requestFacade.sendRequest(1003L, 1004L, "Привет");

        Long requestId = requestRepository
                .findBySenderUserIdAndToUserIdAndStatus(1003L, 1004L, RequestStatus.PENDING)
                .orElseThrow()
                .getRequestId();

        mockMvc.perform(post("/api/v1/request/" + requestId + "/accept").secure(true)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.dialogId").isNumber());

        assertEquals(RequestStatus.ACCEPTED, requestRepository.findById(requestId).orElseThrow().getStatus());
        assertTrue(userRepository.findUsersByFriendsUserId(1003L).stream()
                .anyMatch(u -> u.getUserId().equals(1004L)));
        assertTrue(userRepository.findUsersByFriendsUserId(1004L).stream()
                .anyMatch(u -> u.getUserId().equals(1003L)));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetSentRequests() throws Exception {
        mockMvc.perform(get("/api/v1/requests/sent").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.requests", hasSize(1)))
                .andExpect(jsonPath("$.result.requests[0].requestId").value(800))
                .andExpect(jsonPath("$.result.requests[0].status").value("PENDING"))
                .andExpect(jsonPath("$.result.requests[0].to.userId").value(1002));
    }

    @Test
    @Sql(scripts = {
            SqlScripts.CLEANUP,
            SqlScripts.USERS,
            SqlScripts.MEETINGS
    }, executionPhase = BEFORE_TEST_METHOD)
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDoNotShowMeetingInRecommendation() throws Exception {
        long rejectedBefore = requestRepository.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .count();

        mockMvc.perform(post("/api/v1/requests/meeting/991").secure(true)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(rejectedBefore + 1, requestRepository.findAll().stream()
                .filter(r -> r.getStatus() == RequestStatus.REJECTED)
                .count());
    }
}
