package com.example.meetings.controller.api;

import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.auth.service.TokenService;
import com.example.meetings.plans.model.domain.Plan;
import com.example.meetings.plans.model.domain.PlanStatus;
import com.example.meetings.plans.repository.PlanRepository;
import com.example.meetings.request.model.domain.Request;
import com.example.meetings.request.repository.RequestRepository;
import com.example.meetings.user.repository.UserRepository;
import com.example.meetings.user.service.BusinessEmailSender;
import com.google.firebase.FirebaseApp;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Comparator;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiPlansControllerTest {

    @MockBean
    private FirebaseApp firebaseApp;
    @MockBean
    private RabbitAdmin rabbitAdmin;
    @MockBean
    private BusinessEmailSender businessEmailSender;
    @MockBean
    private TokenService tokenService;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private RequestRepository requestRepository;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    @Test
    @WithUserDetails("user2")
    public void testLiveToggleOnOff() throws Exception {
        mockMvc.perform(post("/api/v1/plans/live/toggle").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":true,\"lat\":55.75,\"lon\":37.61}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.type").value("LIVE"));

        mockMvc.perform(post("/api/v1/plans/live/toggle").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"enabled\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").doesNotExist());
    }

    @Test
    @WithUserDetails("user2")
    public void testCreateScheduled() throws Exception {
        mockMvc.perform(post("/api/v1/plans").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Coffee","description":"Downtown","startAt":"2030-01-01T12:00:00","expiresAt":"2030-01-01T14:00:00","lat":55.75,"lon":37.61}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.type").value("SCHEDULED"))
                .andExpect(jsonPath("$.result.title").value("Coffee"));
    }

    @Test
    @WithUserDetails("user3")
    public void testNearbyFiltersByType() throws Exception {
        planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
                .setType(com.example.meetings.plans.model.domain.PlanType.LIVE)
                .setStatus(PlanStatus.ACTIVE)
                .setExpiresAt(LocalDateTime.now().plusHours(2))
                .setVisibilityRadiusM(3000)
                .setCreatedAt(LocalDateTime.now())
                .setUpdatedAt(LocalDateTime.now())
                .setLat(55.75)
                .setLon(37.61));

        planRepository.save(new Plan()
                .setOwner(userRepository.findById(1004L).orElseThrow())
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
    @WithUserDetails("user3")
    public void testRespondCreatesFriendRequestWithMarker() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
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

        Request created = requestRepository.findAll().stream().max(Comparator.comparing(Request::getRequestId)).orElseThrow();
        assertNotNull(created.getMessage());
        String content = messageRepository.findById(created.getMessage().getMessageId()).orElseThrow().getContent();
        assertTrue(content.contains("[PLAN_RESPONSE]"));
        assertTrue(content.contains("planId=" + plan.getId()));
    }

    @Test
    @WithUserDetails("user3")
    public void testPatchStatusForbiddenForNonOwner() throws Exception {
        Plan plan = planRepository.save(new Plan()
                .setOwner(userRepository.findById(1002L).orElseThrow())
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
}

