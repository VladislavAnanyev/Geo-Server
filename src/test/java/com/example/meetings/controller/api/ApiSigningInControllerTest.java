package com.example.meetings.controller.api;

import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.user.repository.DeviceRepository;
import com.example.meetings.user.repository.UserRepository;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.Assert.assertNotNull;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.SUPPORT_USER
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiSigningInControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DeviceRepository deviceRepository;

    @Test
    public void testSignIn() throws Exception {
        String json = """
                {
                    "username": "user1",
                    "password": "user1",
                    "fcmToken": "123"
                }
                """;

        mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(json)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwtToken").isString())
                .andExpect(jsonPath("$.result.userId").value(1001))
                .andExpect(jsonPath("$.result.refreshToken").isString());

        assertNotNull(deviceRepository.findByFcmToken("123"));
    }

    @Test
    public void testSignInFail() throws Exception {
        String json = """
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
        String phone = "+79057970526";

        mockMvc.perform(post("/api/v1/signin/phone")
                        .secure(true)
                        .contentType(APPLICATION_JSON)
                        .content("{\"phone\": \"" + phone + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertNotNull(userRepository.findUserByUsername(phone).orElse(null));

        String jsonForSignIn = """
                {
                    "username": "+79057970526",
                    "password": "1234"
                }
                """;

        mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(jsonForSignIn)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.result.jwtToken").isString())
                .andExpect(jsonPath("$.result.userId").exists());
    }

    @Test
    public void testSignInViaEmail() throws Exception {
        String email = "test-signin@example.com";

        mockMvc.perform(post("/api/v1/signin/email")
                        .secure(true)
                        .contentType(APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertNotNull(userRepository.findUserByUsername(email).orElse(null));
    }

    @Test
    public void testRefreshToken() throws Exception {
        String signInJson = """
                {
                    "username": "user1",
                    "password": "user1"
                }
                """;

        String response = mockMvc.perform(post("/api/v1/signin").secure(true)
                        .content(signInJson)
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String jwtToken = JsonPath.read(response, "$.result.jwtToken");
        String refreshToken = JsonPath.read(response, "$.result.refreshToken");

        mockMvc.perform(post("/api/v1/refresh")
                        .secure(true)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(APPLICATION_JSON)
                        .content("{\"refreshToken\": \"" + refreshToken + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.jwtToken").isString())
                .andExpect(jsonPath("$.result.refreshToken").isString());
    }
}
