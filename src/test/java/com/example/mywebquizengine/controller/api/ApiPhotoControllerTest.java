package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.userinfo.domain.Photo;
import com.example.mywebquizengine.repos.PhotoRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApiPhotoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PhotoRepository photoRepository;

    @Test
    @WithUserDetails(value = "user5")
    public void testDeletePhotoWhenPhotoSizeIsOne() throws Exception {
        mockMvc.perform(delete("/api/user/photo/69").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAIL"));
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testDeletePhoto() throws Exception {

        Integer photoCountByUsernameBefore = photoRepository.getPhotoCountByUserId(1002L);

        mockMvc.perform(delete("/api/user/photo/65").secure(true))
                .andExpect(status().isOk());

        Integer photoCountByUsernameAfter = photoRepository.getPhotoCountByUserId(1002L);

        assertNotEquals(photoCountByUsernameBefore, photoCountByUsernameAfter);

        List<Photo> userPhotos = photoRepository.findByUser_UserId(1002L);
        int position = userPhotos.get(0).getPosition();

        assertEquals(0, position);
    }

    @Test
    @WithUserDetails(value = "user3")
    public void testDeletePhotoForbidden() throws Exception {
        mockMvc.perform(delete("/api/user/photo/68").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testDeleteNotExistPhoto() throws Exception {
        mockMvc.perform(delete("/api/user/photo/670").secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testSwapPhoto() throws Exception {
        List<Photo> userPhotoBefore = photoRepository.findByUser_UserId(1001L);

        mockMvc.perform(post("/api/user/photo/swap?photoId=63&position=2").secure(true))
                .andExpect(status().isOk());

        List<Photo> userPhotos = photoRepository.findByUser_UserId(1001L);
        assertNotEquals(userPhotos.get(0).getUrl(), userPhotoBefore.get(0).getUrl());
        assertEquals("https://localhost/img/1.jpg", userPhotos.get(0).getUrl());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testForbiddenSwap() throws Exception {
        mockMvc.perform(post("/api/user/photo/swap?photoId=67&position=2").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testNotFoundSwap() throws Exception {
        mockMvc.perform(post("/api/user/photo/swap?photoId=6896&position=4").secure(true))
                .andExpect(status().isNotFound());
    }

}