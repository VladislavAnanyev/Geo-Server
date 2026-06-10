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
        SqlScripts.LOCATIONS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiLocationControllerTest extends AbstractControllerIntegrationTest {

    @Test
    public void getLocationsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/locations").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLocations() throws Exception {
        mockMvc.perform(get("/api/v1/locations").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.locations", hasSize(1)))
                .andExpect(jsonPath("$.locations[0].id").value(1))
                .andExpect(jsonPath("$.locations[0].name").value("Test location"))
                .andExpect(jsonPath("$.locations[0].lat").value(55.75))
                .andExpect(jsonPath("$.locations[0].lng").value(37.61))
                .andExpect(jsonPath("$.locations[0].radius").value(1000));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLocationPeopleRequiresPremium() throws Exception {
        mockMvc.perform(get("/api/v1/locations/1/people").secure(true))
                .andExpect(status().isPaymentRequired());
    }

    @Test
    @Sql(scripts = {
            SqlScripts.CLEANUP,
            SqlScripts.USERS,
            SqlScripts.LOCATIONS,
            SqlScripts.SUBSCRIPTIONS_PREMIUM
    }, executionPhase = BEFORE_TEST_METHOD)
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getLocationPeopleWithPremium() throws Exception {
        mockMvc.perform(get("/api/v1/locations/1/people").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.people").isArray());
    }
}
