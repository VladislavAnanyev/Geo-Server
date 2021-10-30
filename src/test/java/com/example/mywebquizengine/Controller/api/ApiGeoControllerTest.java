package com.example.mywebquizengine.Controller.api;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ApiGeoController.class)
public class ApiGeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /*@MockBean
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Test
    public void test1() throws Exception {
        mockMvc.perform(get("/api/test")).andExpect(status().isOk());
    }*/
}