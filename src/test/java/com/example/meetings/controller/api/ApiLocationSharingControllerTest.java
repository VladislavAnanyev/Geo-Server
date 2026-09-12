package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.geolocation.repository.GeolocationRepository;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus;
import com.example.meetings.geolocation.sharing.repository.LocationSharingRepository;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.geolocation.sharing.facade.LocationSharingFacade;
import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.GEOLOCATIONS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiLocationSharingControllerTest extends AbstractControllerIntegrationTest {

    private static final long USER1_ID = 1001L;
    private static final long USER2_ID = 1002L;

    @Autowired
    private LocationSharingRepository locationSharingRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private LocationSharingFacade locationSharingFacade;

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void friendsGeolocationsEmptyWithoutConsent() {
        assertTrue(geolocationService.getFriendsGeolocations(USER1_ID).isEmpty());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void acceptInviteEnablesMutualGeolocation() throws Exception {
        mockMvc.perform(post("/api/v1/geolocation-sharing/request").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"friendUserId\": " + USER2_ID + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(1, locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING).size());

        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();

        locationSharingFacade.accept(USER2_ID, sharingId);

        assertEquals(1, geolocationService.getFriendsGeolocations(USER1_ID).size());
        assertEquals(1, geolocationService.getFriendsGeolocations(USER2_ID).size());
        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isPresent());
    }

    @Test
    public void mutualPendingInvitesAutoActivate() {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        locationSharingFacade.sendInvite(USER2_ID, new LocationSharingFriendRequest().setFriendUserId(USER1_ID));

        assertEquals(1, geolocationService.getFriendsGeolocations(USER1_ID).size());
        assertEquals(1, geolocationService.getFriendsGeolocations(USER2_ID).size());
        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isPresent());
    }

    @Test
    public void revokeStopsGeolocationSharing() {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();
        locationSharingFacade.accept(USER2_ID, sharingId);

        locationSharingFacade.revoke(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));

        assertTrue(geolocationService.getFriendsGeolocations(USER1_ID).isEmpty());
        assertTrue(geolocationService.getFriendsGeolocations(USER2_ID).isEmpty());
        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isEmpty());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getActiveEndpointReturnsAcceptedSharing() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();
        locationSharingFacade.accept(USER2_ID, sharingId);

        mockMvc.perform(get("/api/v1/geolocation-sharing/active").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.items[0].initiator.userId").value(USER1_ID))
                .andExpect(jsonPath("$.items[0]['target'].userId").value(USER2_ID));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void outgoingIncomingRequestsCanBeFilteredByUserId() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/outgoing")
                        .queryParam("userId", String.valueOf(USER2_ID))
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].status").value("PENDING"))
                .andExpect(jsonPath("$.items[0]['target'].userId").value(USER2_ID));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/outgoing")
                        .queryParam("userId", "999999")
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void incomingRequestsCanBeFilteredByUserId() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/incoming")
                        .queryParam("userId", String.valueOf(USER1_ID))
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].initiator.userId").value(USER1_ID))
                .andExpect(jsonPath("$.items[0].status").value("PENDING"));
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void acceptInviteViaHttp() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();

        mockMvc.perform(post("/api/v1/geolocation-sharing/request/" + sharingId + "/accept").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isPresent());
        assertEquals(1, geolocationService.getFriendsGeolocations(USER1_ID).size());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void revokeViaHttp() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();
        locationSharingFacade.accept(USER2_ID, sharingId);

        mockMvc.perform(post("/api/v1/geolocation-sharing/revoke").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"friendUserId\": " + USER2_ID + "}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isEmpty());
        assertTrue(geolocationService.getFriendsGeolocations(USER1_ID).isEmpty());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void rejectInviteViaHttp() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();

        mockMvc.perform(post("/api/v1/geolocation-sharing/request/" + sharingId + "/reject").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertTrue(locationSharingRepository.findActiveBetween(USER1_ID, USER2_ID).isEmpty());
        assertEquals(0, locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING).size());
    }
}
