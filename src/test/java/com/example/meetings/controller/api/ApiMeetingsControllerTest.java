package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import org.junit.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.MEETINGS,
        SqlScripts.SUBSCRIPTIONS_PREMIUM
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiMeetingsControllerTest extends AbstractControllerIntegrationTest {

    @Test
    public void getMeetingsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/meetings").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getMeetings() throws Exception {
        mockMvc.perform(get("/api/v1/meetings").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.meetings", hasSize(2)))
                .andExpect(jsonPath("$.result.meetings[*].meetingId").value(org.hamcrest.Matchers.hasItems(991, 993)))
                .andExpect(jsonPath("$.result.meetings[0].lat").exists())
                .andExpect(jsonPath("$.result.meetings[0].lng").exists())
                .andExpect(jsonPath("$.result.meetings[0].user.userId").exists());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getMeetingHistory() throws Exception {
        mockMvc.perform(get("/api/v1/meetings/history").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.times", hasSize(2)));
    }
}
