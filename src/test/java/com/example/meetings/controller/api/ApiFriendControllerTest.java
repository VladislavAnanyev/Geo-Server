package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiFriendControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void getFriendsWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/friends").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getFriends() throws Exception {
        mockMvc.perform(get("/api/v1/friends").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[*].userId").value(org.hamcrest.Matchers.hasItems(1002, 1003, 1004)))
                .andExpect(jsonPath("$[0].username").exists())
                .andExpect(jsonPath("$[0].firstName").exists());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteFriend() throws Exception {
        assertEquals(3, userRepository.findUsersByFriendsUserId(1001L).size());

        mockMvc.perform(delete("/api/v1/friend/1004").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(2, userRepository.findUsersByFriendsUserId(1001L).size());
        assertTrue(userRepository.findUsersByFriendsUserId(1001L).stream()
                .noneMatch(u -> u.getUserId().equals(1004L)));
    }
}
