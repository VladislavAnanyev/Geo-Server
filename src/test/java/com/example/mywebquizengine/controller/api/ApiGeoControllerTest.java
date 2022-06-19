package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.geo.dto.output.MeetingViewCustomQuery;
import com.example.mywebquizengine.repos.MeetingRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiGeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    @WithUserDetails(value = "user5")
    public void testSendGeolocationInWestAndAssertThatMeetingCreate() throws Exception {
        String json = """
                {
                    "lat": 61.876225,
                    "lng": 75.35547     
                }
                """;


        mockMvc.perform(post("/api/sendGeolocation").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Timestamp timestamp;
        timestamp = Timestamp.from(new GregorianCalendar().toInstant());
        String time = timestamp.toString();

        List<MeetingViewCustomQuery> myMeetings = meetingRepository.getMyMeetings(1005L, time.substring(0, 10));
        assertEquals(2, myMeetings.size());
    }


}