package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.repos.RequestRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.sql.Timestamp;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApiRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RequestRepository requestRepository;

    @Test
    @WithUserDetails(value = "user3")
    public void testSendRequest() throws Exception {

        String json = """
                {
                    "toUsername": "user4",
                    "meetingId": 993,
                    "messageContent": "Привет"
                }
                """;

        mockMvc.perform(post("/api/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }


    @Test
    @WithUserDetails(value = "user3")
    public void testSendRequestWithoutMessage() throws Exception {

        String json = """
                {
                    "toUsername": "user4",
                    "meetingId": 990
                }
                """;

        mockMvc.perform(post("/api/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


    }

    @Test
    @WithUserDetails(value = "user2")
    public void testRejectRequest() throws Exception {
        mockMvc.perform(post("/api/request/800/reject").secure(true)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testSendRequestWhenAlreadyFriends() throws Exception {

        String json = """
                {
                    "toUsername": "user5",
                    "meetingId": 992,
                    "messageContent": "Привет"
                }
                """;

        mockMvc.perform(post("/api/request").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


}