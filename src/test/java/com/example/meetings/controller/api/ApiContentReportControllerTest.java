package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.report.model.domain.ContentReportTargetType;
import com.example.meetings.report.repository.ContentReportRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiContentReportControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private ContentReportRepository contentReportRepository;

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void createReport() throws Exception {
        assertEquals(0, contentReportRepository.count());

        mockMvc.perform(post("/api/v1/reports").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"targetType":"USER","targetId":1002}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(1, contentReportRepository.count());
        assertTrue(contentReportRepository.existsByReporter_UserIdAndTargetTypeAndTargetId(
                1001L, ContentReportTargetType.USER, 1002L));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void createDuplicateReportIsIdempotent() throws Exception {
        String body = """
                {"targetType":"USER","targetId":1002}
                """;

        mockMvc.perform(post("/api/v1/reports").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(post("/api/v1/reports").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(1, contentReportRepository.count());
    }
}
