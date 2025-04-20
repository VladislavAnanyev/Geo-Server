package com.example.meetings.controller.api;

import com.example.meetings.request.repository.RequestRepository;
import com.google.firebase.ErrorCode;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiRequestControllerTest {

    @MockBean
    private FirebaseApp firebaseApp;

    @MockBean
    private FirebaseMessaging fcm;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @WithUserDetails(value = "user3")
    public void testSendRequest() throws Exception {

        requestRepository.deleteAll();

        String json = """
                {
                    "toUserId": 1004,
                    "meetingId": 993,
                    "messageContent": "Привет"
                }
                """;

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

    @Test
    @WithUserDetails(value = "user3")
    public void testSendRequestWithoutMessage() throws Exception {
        String json = """
                {
                    "toUserId": 1004,
                    "meetingId": 990
                }
                """;

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testRejectRequest() throws Exception {
        mockMvc.perform(post("/api/v1/request/800/reject").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testSendRequestWhenAlreadyFriends() throws Exception {

        String json = """
                {
                    "toUserId": 1005,
                    "meetingId": 992,
                    "messageContent": "Привет"
                }
                """;

        mockMvc.perform(post("/api/v1/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}