package com.example.meetings.controller.api;

import com.example.meetings.auth.service.TokenService;
import com.example.meetings.user.model.GetAuthCodeResponse;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import com.example.meetings.user.service.BusinessEmailSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiUserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private RabbitAdmin rabbitAdmin;

    @MockBean
    private BusinessEmailSender businessEmailSender;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetAuthUserWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/auth").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails("user1")
    public void testGetAuthUserWithAuth() throws Exception {
        mockMvc.perform(get("/api/v1/user/auth").secure(true))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    public void testSignIn() throws Exception {
        doAnswer(invocationOnMock -> null).when(rabbitAdmin).declareExchange(anyObject());

        String json =
                """
                                {
                                    "username": "user1",
                                    "password": "user1"
                                }
                        """;

        mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jwtToken").isString());
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

        mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("FAIL"));
    }

    @Test
    public void testGetCodeForSignInViaPhone() throws Exception {

        doAnswer(invocationOnMock -> null).when(rabbitAdmin).declareExchange(anyObject());

        // Регистрация при помощи номера телефона, возвращается (отправляется смс) код для входа
        String json = """
                    {
                        "phone": "+7(905)7970526",
                        "firstName": "Владислав",
                        "lastName": "Ананьев"
                    }
                """;

        mockMvc.perform(post("/api/v1/signin/phone")
                        .secure(true)
                        .contentType(APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        User user = userRepository.findUserByUsername("+7(905)7970526").get();
        assertNotNull(user);

        String jsonForRequestCodeAgain = """
                    {
                        "phone": "+7(905)7970526"
                    }
                """;

        String responseCodeAgain = mockMvc.perform(post("/api/v1/signin/phone")
                        .secure(true)
                        .contentType(APPLICATION_JSON)
                        .content(jsonForRequestCodeAgain))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        GetAuthCodeResponse getAuthCodeResponse = objectMapper.readValue(responseCodeAgain, GetAuthCodeResponse.class);

        // Вход по полученному коду
        String jsonForSignIn = String.format(
                """
                                {
                                    "username": "+7(905)7970526",
                                    "password": "%s"
                                }
                        """, getAuthCodeResponse.getResult().getCode());

        mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(jsonForSignIn)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jwtToken").isString());
    }

}