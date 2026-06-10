package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.photo.repository.PhotoRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiPhotoControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @WithUserDetails(value = "user5", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeletePhotoWhenPhotoSizeIsOne() throws Exception {
        mockMvc.perform(delete("/api/v1/user/photo/69").secure(true))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("FAIL"));
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeletePhoto() throws Exception {
        Integer photoCountByUsernameBefore = photoRepository.getPhotoCountByUserId(1002L);

        mockMvc.perform(delete("/api/v1/user/photo/65").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertTrue(photoRepository.findById(65L).isEmpty());

        Integer photoCountByUsernameAfter = photoRepository.getPhotoCountByUserId(1002L);

        assertNotEquals(photoCountByUsernameBefore, photoCountByUsernameAfter);

        List<Photo> userPhotos = photoRepository.findPhotosByUserId(1002L);
        assertEquals(Integer.valueOf(0), userPhotos.get(0).getPosition());
    }

    @Test
    @WithUserDetails(value = "user3", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeletePhotoForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/user/photo/68").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeleteNotExistPhoto() throws Exception {
        mockMvc.perform(delete("/api/v1/user/photo/670").secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testSwapPhoto() throws Exception {
        List<Photo> userPhotoBefore = photoRepository.findPhotosByUserId(1001L);

        mockMvc.perform(post("/api/v1/user/photo/swap?photoId=63&position=2").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        List<Photo> userPhotos = photoRepository.findPhotosByUserId(1001L);
        assertNotEquals(userPhotos.get(0).getUrl(), userPhotoBefore.get(0).getUrl());
        assertEquals("https://localhost/img/1.jpg", userPhotos.get(0).getUrl());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testForbiddenSwap() throws Exception {
        mockMvc.perform(post("/api/v1/user/photo/swap?photoId=67&position=2").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testNotFoundSwap() throws Exception {
        mockMvc.perform(post("/api/v1/user/photo/swap?photoId=6896&position=4").secure(true))
                .andExpect(status().isNotFound());
    }
}
