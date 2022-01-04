package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.repos.UserRepository;
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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApiUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private RabbitAdmin rabbitAdmin;

    @Test
    public void testGetAuthUserWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/user/auth").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("user1")
    public void testGetAuthUserWithAuth() throws Exception {
        mockMvc.perform(get("/api/authuser").secure(true))
                .andExpect(status().isOk());
    }


    @Test
    public void testSignUp() throws Exception {

        doAnswer(i -> {
            return null;
        }).when(rabbitAdmin).declareExchange(anyObject());

        String json =
                """
                        {
                            "username": "application",
                            "email": "a.vlad.v@ya.ru",
                            "firstName": "Владислав",
                            "lastName": "Ананьев",
                            "password": "12345"
                        }
                        """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isString());

        User user = userRepository.findById("application").get();
        assertNotNull(user.getPassword());

    }

    @Test
    public void testSignUpWithExistUsername() throws Exception {

        String json = """
                {   
                    "username": "user1",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadPassword() throws Exception {

        String json = """
                {
                    "username": "application",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadUsername() throws Exception {

        String json = """
                {
                    "username": "    ",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithSpaceInUsername() throws Exception {

        String json = """
                {
                    "username": "appli cation",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithoutEmail() throws Exception {

        String json = """
                {
                    "username": "application",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadEmail() throws Exception {

        String json = """
                {   
                    "username": "application",
                    "email": "a.vlad.c@ya.",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBlankEmail() throws Exception {

        String json = """
                {   
                    "username": "application",
                    "email": "",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithoutFirstName() throws Exception {

        String json = """
                {   
                    "username": "application",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "",
                    "lastName": "Ананьев",
                    "password": "12345"
                }
                """;

        mockMvc.perform(post("/api/signup").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }


    @Test
    public void testSignIn() throws Exception {

        doAnswer(i -> {
            return "123";
        }).when(rabbitAdmin).declareQueue(anyObject());

        doAnswer(i -> {
            return null;
        }).when(rabbitAdmin).declareBinding(anyObject());

        String json =
                """
                        {
                            "username": "user1",
                            "password": "user1"
                        }
                """;

        mockMvc.perform(post("/api/signin").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isString())
                .andExpect(jsonPath("$.queueName").isString());
    }


    @Test
    public void testSignInFail() throws Exception {

        String json =
                """
                        {
                            "username": "user1",
                            "password": "12345"
                        }
                """;

        mockMvc.perform(post("/api/signin").secure(true)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testCheckExistUsername() throws Exception {
        String status = mockMvc.perform(get("/api/user/check-username?username=user1").secure(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertTrue(Boolean.parseBoolean(status));
    }

    @Test
    public void testCheckNotExistUsername() throws Exception {
        String status = mockMvc.perform(get("/api/user/check-username?username=user100").secure(true))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertFalse(Boolean.parseBoolean(status));
    }


    @Test
    @WithUserDetails("user1")
    public void testChangeUserData() throws Exception {
        String json =
                """
                    {
                        "firstName": "rename",
                        "lastName": "user"
                    }
                """;
        mockMvc.perform(put("/api/user").secure(true).contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        User user = userRepository.findById("user1").get();
        assertEquals("rename", user.getFirstName());
        assertEquals("user", user.getLastName());
    }

    @Test
    @WithUserDetails("user1")
    public void testFailChangeUserData() throws Exception {
        String json =
                """
                    {
                        "firstName": "",
                        "lastName": "user"
                    }
                """;
        mockMvc.perform(put("/api/user").secure(true).contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());

        User user = userRepository.findById("user1").get();
        assertEquals("user1", user.getFirstName());
        assertEquals("user1", user.getLastName());
    }

}