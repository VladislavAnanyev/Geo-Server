package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiFavoritePlaceControllerTest extends AbstractControllerIntegrationTest {

    private static final String PLACE_JSON = """
            {"name":"Парк Горького","lat":55.731,"lng":37.601}
            """;

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void addFavoritePlace() throws Exception {
        mockMvc.perform(post("/api/v1/user/favorite-places").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLACE_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.name").value("Парк Горького"))
                .andExpect(jsonPath("$.result.lat").value(55.731))
                .andExpect(jsonPath("$.result.lng").value(37.601))
                .andExpect(jsonPath("$.result.id").isNumber());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void addDuplicateFavoritePlaceReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/v1/user/favorite-places").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLACE_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/user/favorite-places").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLACE_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteOwnFavoritePlace() throws Exception {
        String response = mockMvc.perform(post("/api/v1/user/favorite-places").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLACE_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long id = JsonPath.parse(response).read("$.result.id", Long.class);

        mockMvc.perform(delete("/api/v1/user/favorite-places/" + id).secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/api/v1/user/1001/profile").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.favoritePlaces").isEmpty());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    @Sql(scripts = SqlScripts.FAVORITE_PLACES, executionPhase = BEFORE_TEST_METHOD)
    public void deleteAnotherUsersFavoritePlaceReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/user/favorite-places/5001").secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void deleteNonExistingFavoritePlaceReturnsNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/user/favorite-places/999999").secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void favoritePlaceVisibleInProfile() throws Exception {
        mockMvc.perform(post("/api/v1/user/favorite-places").secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(PLACE_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/user/1001/profile").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.favoritePlaces.length()").value(1))
                .andExpect(jsonPath("$.result.favoritePlaces[0].name").value("Парк Горького"));
    }
}
