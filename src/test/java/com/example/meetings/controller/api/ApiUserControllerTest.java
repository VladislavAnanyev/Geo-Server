package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import org.junit.Test;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiUserControllerTest extends AbstractControllerIntegrationTest {

    @Test
    public void testGetAuthUserWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/auth").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetAuthUserWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/auth").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(1001))
                .andExpect(jsonPath("$.result.username").value("user1"))
                .andExpect(jsonPath("$.result.firstName").value("user1"))
                .andExpect(jsonPath("$.result.photos").isArray())
                .andExpect(jsonPath("$.result.photos.length()").value(3));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetUserProfile() throws Exception {
        mockMvc.perform(get("/api/v1/user/1002/profile").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(1002))
                .andExpect(jsonPath("$.result.username").value("user2"))
                .andExpect(jsonPath("$.result.firstName").value("user2"))
                .andExpect(jsonPath("$.result.photos").isArray());
    }
}
