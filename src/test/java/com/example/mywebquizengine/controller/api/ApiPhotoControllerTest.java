package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.repos.PhotoRepository;
import com.example.mywebquizengine.repos.UserRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @WithUserDetails(value = "user1")
    public void testDeletePhotoWhenPhotoSizeIsOne() throws Exception {
        mockMvc.perform(delete("/api/user/photo/64").secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testDeletePhoto() throws Exception {

        Integer photoCountByUsernameBefore = photoRepository.getPhotoCountByUsername("user2");

        mockMvc.perform(delete("/api/user/photo/66").secure(true))
                .andExpect(status().isOk());

        Integer photoCountByUsernameAfter = photoRepository.getPhotoCountByUsername("user2") + 1;

        assertEquals(photoCountByUsernameBefore, photoCountByUsernameAfter);

    }

}