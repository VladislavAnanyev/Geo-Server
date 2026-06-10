package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.chat.repository.DialogRepository;
import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.plans.model.domain.*;
import com.example.meetings.plans.repository.PlanRepository;
import com.example.meetings.plans.repository.PlanResponseRepository;
import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.model.domain.RequestStatus;
import com.example.meetings.request.repository.RequestRepository;
import com.example.meetings.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.meetings.plans.model.domain.PlanType.LIVE;
import static org.junit.Assert.*;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.SUBSCRIPTIONS_PREMIUM,
        SqlScripts.SUPPORT_USER,
        SqlScripts.DIALOGS,
        SqlScripts.MESSAGES_1224,
        SqlScripts.DIALOG_UPDATE_1224
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiPlansControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private DialogRepository dialogRepository;
    @Autowired
    private PlanResponseRepository planResponseRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testLiveToggleOnOff() throws Exception {
        long activeLiveBefore = planRepository.findAll().stream()
                .filter(p -> p.getType() == LIVE
                        && p.getStatus() == PlanStatus.ACTIVE)
                .count();

        mockMvc.perform(post("/api/v1/plans/live/toggle").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":true,\"lat\":55.75,\"lon\":37.61}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.type").value("LIVE"))
                .andExpect(jsonPath("$.result.user.userId").value(1002))
                .andExpect(jsonPath("$.result.ownPlan").value(true));

        assertEquals(activeLiveBefore + 1, planRepository.findAll().stream()
                .filter(p -> p.getType() == LIVE
                        && p.getStatus() == PlanStatus.ACTIVE)
                .count());

        mockMvc.perform(post("/api/v1/plans/live/toggle").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.type").value("LIVE"));
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testCreateScheduled() throws Exception {
        long countBefore = planRepository.count();

        mockMvc.perform(post("/api/v1/plans").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Coffee","description":"Downtown","startAt":"2030-01-01T12:00:00","expiresAt":"2030-01-01T14:00:00","lat":55.75,"lon":37.61,"mode":"PERSONAL"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.type").value("SCHEDULED"))
                .andExpect(jsonPath("$.result.title").value("Coffee"))
                .andExpect(jsonPath("$.result.user.userId").value(1002))
                .andExpect(jsonPath("$.result.ownPlan").value(true));

        assertEquals(countBefore + 1, planRepository.count());
        assertTrue(planRepository.findAll().stream()
                .anyMatch(p -> "Coffee".equals(p.getTitle()) && p.getOwner().getUserId().equals(1002L)));
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testNearbyFiltersByType() throws Exception {
        planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setMode(PlanMode.PERSONAL)
                .setType(LIVE)
                .setStatus(PlanStatus.ACTIVE)
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.75)
                .setLon(37.61));

        planRepository.save(new Plan()
                .setOwner(userRepository.findById(1004L).orElseThrow())
                .setMode(PlanMode.PERSONAL)
                .setType(com.example.meetings.plans.model.domain.PlanType.SCHEDULED)
                .setStatus(PlanStatus.ACTIVE)
                .setTitle("Sched")
                .setStartAt(LocalDateTime.now().plusHours(1))
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.751)
                .setLon(37.611));

        mockMvc.perform(get("/api/v1/plans/nearby").secure(true)
                        .param("lat", "55.75")
                        .param("lon", "37.61")
                        .param("radiusM", "5000")
                        .param("type", "LIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.items[0].type").value("LIVE"));
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testRespondCreatesFriendRequestWithMarker() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setMode(PlanMode.PERSONAL)
                .setType(com.example.meetings.plans.model.domain.PlanType.SCHEDULED)
                .setStatus(PlanStatus.ACTIVE)
                .setTitle("Test")
                .setStartAt(LocalDateTime.now().plusHours(1))
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.75)
                .setLon(37.61));

        mockMvc.perform(post("/api/v1/plans/" + plan.getId() + "/respond").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"hello\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.planId").value(plan.getId()));

        Request created = requestRepository
                .findBySenderUserIdAndToUserIdAndStatus(1003L, 1002L, RequestStatus.PENDING)
                .orElseThrow();
        assertNotNull(created.getMessage());
        String content = messageRepository.findById(created.getMessage().getMessageId()).orElseThrow().getContent();
        assertTrue(content.contains("Отклик на план"));
        assertTrue(content.contains("Test"));
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testPatchStatusForbiddenForNonOwner() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setMode(PlanMode.PERSONAL)
                .setType(com.example.meetings.plans.model.domain.PlanType.SCHEDULED)
                .setStatus(PlanStatus.ACTIVE)
                .setTitle("Test")
                .setStartAt(LocalDateTime.now().plusHours(1))
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now()));

        mockMvc.perform(patch("/api/v1/plans/" + plan.getId() + "/status").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CLOSED\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGroupPlanRespondCreatesPlanResponse() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setType(com.example.meetings.plans.model.domain.PlanType.SCHEDULED)
                .setMode(PlanMode.GROUP)
                .setStatus(PlanStatus.ACTIVE)
                .setTitle("Group")
                .setStartAt(LocalDateTime.now().plusHours(1))
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setDialog(dialogRepository.getById(1224L))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.75)
                .setLon(37.61));

        mockMvc.perform(post("/api/v1/plans/" + plan.getId() + "/respond").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"message\":\"join me\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.planId").value(plan.getId()))
                .andExpect(jsonPath("$.result.status").value("PENDING"));

        assertTrue(planResponseRepository.findAll().stream()
                .anyMatch(r -> r.getPlan().getId().equals(plan.getId())
                        && r.getUser().getUserId().equals(1003L)
                        && r.getStatus() == PlanResponseStatus.PENDING));
    }

    @Test
    @Transactional
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGroupPlanApproveAddsUserToDialog() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setType(com.example.meetings.plans.model.domain.PlanType.SCHEDULED)
                .setMode(PlanMode.GROUP)
                .setStatus(PlanStatus.ACTIVE)
                .setTitle("Group")
                .setStartAt(LocalDateTime.now().plusHours(1))
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setDialog(dialogRepository.getById(1224L))
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.75)
                .setLon(37.61));
        PlanResponse response = planResponseRepository.save(new PlanResponse()
                .setPlan(plan)
                .setUser(userRepository.findById(1003L).orElseThrow())
                .setStatus(PlanResponseStatus.PENDING)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now()));

        mockMvc.perform(post("/api/v1/plans/" + plan.getId() + "/responses/" + response.getId() + "/approve").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.status").value("APPROVED"));

        assertEquals(PlanResponseStatus.APPROVED,
                planResponseRepository.findById(response.getId()).orElseThrow().getStatus());
        assertTrue(dialogRepository.findById(1224L).orElseThrow().getUsers().stream()
                .anyMatch(u -> u.getUserId().equals(1003L)));
    }
}

