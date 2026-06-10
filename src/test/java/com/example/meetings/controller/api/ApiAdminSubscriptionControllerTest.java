package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.subscription.model.domain.SubscriptionStatus;
import com.example.meetings.subscription.repository.UserSubscriptionRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.SUPPORT_USER,
        SqlScripts.SUBSCRIPTIONS_PREMIUM
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiAdminSubscriptionControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserSubscriptionRepository userSubscriptionRepository;

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void grantSubscriptionForbiddenForNonAdmin() throws Exception {
        long countBefore = userSubscriptionRepository.findAll().stream()
                .filter(s -> s.getUser().getUserId().equals(1002L))
                .count();

        mockMvc.perform(post("/api/v1/admin/users/1002/subscription").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"planCode":"premium","durationDays":30}
                                """))
                .andExpect(status().isForbidden());

        assertTrue(countBefore > 0);
        assertTrue(userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
                        1002L, SubscriptionStatus.ACTIVE, LocalDateTime.now())
                .isPresent());
    }

    @Test
    @WithUserDetails(value = "support", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void grantSubscriptionAsAdmin() throws Exception {
        LocalDateTime expiresBefore = userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
                        1002L, SubscriptionStatus.ACTIVE, LocalDateTime.now())
                .orElseThrow()
                .getExpiresAt();

        mockMvc.perform(post("/api/v1/admin/users/1002/subscription").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"planCode":"premium","durationDays":30}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.userId").value(1002))
                .andExpect(jsonPath("$.result.active").value(true))
                .andExpect(jsonPath("$.result.planCode").value("premium"));

        LocalDateTime expiresAfter = userSubscriptionRepository
                .findFirstByUserUserIdAndStatusAndExpiresAtAfterOrderByExpiresAtDesc(
                        1002L, SubscriptionStatus.ACTIVE, LocalDateTime.now())
                .orElseThrow()
                .getExpiresAt();
        assertTrue(expiresAfter.isAfter(expiresBefore));
    }
}
