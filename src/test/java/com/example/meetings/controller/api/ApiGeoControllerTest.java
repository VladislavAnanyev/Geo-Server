package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.geolocation.repository.GeolocationRepository;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.GEOLOCATIONS,
        SqlScripts.MEETINGS,
        SqlScripts.SUBSCRIPTIONS_PREMIUM
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiGeoControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private GeolocationRepository geolocationRepository;

    @Test
    @WithUserDetails(value = "user5", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testSendGeolocationInWestAndAssertThatMeetingCreate() throws Exception {
        String json = """
                {
                    "lat": 61.876225,
                    "lng": 75.35547
                }
                """;

        mockMvc.perform(post("/api/v1/geolocation").secure(true)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertTrue(geolocationRepository.findLatestByUserId(1005L).isPresent());
        assertEquals(61.876225, geolocationRepository.findLatestByUserId(1005L).orElseThrow().getLat(), 0.0001);
        assertEquals(75.35547, geolocationRepository.findLatestByUserId(1005L).orElseThrow().getLng(), 0.0001);

        Thread.sleep(5000L);

        List<MeetingView> myMeetings = meetingRepository.getMyMeetings(
                1005L,
                LocalDate.now().atStartOfDay(),
                LocalDate.now().plusDays(1).atStartOfDay()
        );
        assertEquals(2, myMeetings.size());
    }

    @Test
    public void testSendGeolocationWithoutAuth() throws Exception {
        mockMvc.perform(post("/api/v1/geolocation").secure(true)
                        .content("{\"lat\": 55.75, \"lng\": 37.61}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
