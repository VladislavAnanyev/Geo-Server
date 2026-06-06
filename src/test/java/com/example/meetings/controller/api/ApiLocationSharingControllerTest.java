package com.example.meetings.controller.api;

import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.geolocation.sharing.facade.LocationSharingFacade;
import com.example.meetings.geolocation.sharing.model.domain.LocationSharingStatus;
import com.example.meetings.geolocation.sharing.model.dto.input.LocationSharingFriendRequest;
import com.example.meetings.geolocation.sharing.repository.LocationSharingRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiLocationSharingControllerTest {

    private static final long USER1_ID = 1001L;
    private static final long USER2_ID = 1002L;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LocationSharingRepository locationSharingRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private LocationSharingFacade locationSharingFacade;

    @MockBean
    private FirebaseApp firebaseApp;

    @MockBean
    private FirebaseMessaging fcm;

    @Before
    public void setUp() {
        locationSharingRepository.deleteAll();
    }

    @Test
    @WithUserDetails("user1")
    public void friendsGeolocationsEmptyWithoutConsent() {
        assertTrue(geolocationService.getFriendsGeolocations(USER1_ID).isEmpty());
    }

    @Test
    @WithUserDetails("user1")
    public void acceptInviteEnablesMutualGeolocation() throws Exception {
        mockMvc.perform(post("/api/v1/geolocation-sharing/request").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"friendUserId\": " + USER2_ID + "}"))
                .andExpect(status().isOk());

        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();

        locationSharingFacade.accept(USER2_ID, sharingId);

        assertEquals(1, geolocationService.getFriendsGeolocations(USER1_ID).size());
        assertEquals(1, geolocationService.getFriendsGeolocations(USER2_ID).size());
    }

    @Test
    public void mutualPendingInvitesAutoActivate() {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        locationSharingFacade.sendInvite(USER2_ID, new LocationSharingFriendRequest().setFriendUserId(USER1_ID));

        assertEquals(1, geolocationService.getFriendsGeolocations(USER1_ID).size());
        assertEquals(1, geolocationService.getFriendsGeolocations(USER2_ID).size());
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
    }

    @Test
    @WithUserDetails("user1")
    public void getActiveEndpointReturnsAcceptedSharing() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));
        Long sharingId = locationSharingRepository
                .findAllByTarget_UserIdAndStatus(USER2_ID, LocationSharingStatus.PENDING)
                .get(0)
                .getSharingId();
        locationSharingFacade.accept(USER2_ID, sharingId);

        mockMvc.perform(get("/api/v1/geolocation-sharing/active").secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @WithUserDetails("user1")
    public void outgoingIncomingRequestsCanBeFilteredByUserId() throws Exception {
        locationSharingFacade.sendInvite(USER1_ID, new LocationSharingFriendRequest().setFriendUserId(USER2_ID));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/outgoing")
                        .queryParam("userId", String.valueOf(USER2_ID))
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/outgoing")
                        .queryParam("userId", "999999")
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(0));

        mockMvc.perform(get("/api/v1/geolocation-sharing/requests/incoming")
                        .queryParam("userId", String.valueOf(USER1_ID))
                        .with(org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user("user2"))
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items.length()").value(1));
    }
}
