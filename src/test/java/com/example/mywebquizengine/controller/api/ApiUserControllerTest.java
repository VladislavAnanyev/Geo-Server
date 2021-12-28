package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.model.Role;
import com.example.mywebquizengine.model.User;
import com.example.mywebquizengine.repos.UserRepository;
import com.example.mywebquizengine.service.JWTUtil;
import org.junit.After;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
/*@EnableAutoConfiguration(exclude = RabbitAutoConfiguration.class)*/
public class ApiUserControllerTest {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository repository;

    @Autowired
    private JWTUtil jwtUtil;

    @MockBean
    private RabbitAdmin rabbitAdmin;

    @After
    public void resetDb() {
        repository.deleteAll();
    }

    @Test
    public void testGetAuthUserWithoutAuth() throws Exception {
        mockMvc.perform(get("https://localhost/api/authuser"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetAuthUserWithAuth() throws Exception {

        User user = createTestPerson("application");
        String token = jwtUtil.generateToken(user);
        System.out.println(token);
        mockMvc.perform(get("https://localhost/api/authuser")
                .header("Authorization",
        "Bearer " + token))
                .andExpect(status().isOk());
    }

    // Try to retrieve user which not exist in database
    @Test
    public void testFailAuthUser() throws Exception {

        User user = createTestPersonWithoutSave("NotApplication");
        String token = jwtUtil.generateToken(user);
        System.out.println(token);
        mockMvc.perform(get("https://localhost/api/authuser")
                .header("Authorization",
                        "Bearer " + token))
                .andExpect(status().isNotFound());
        //.andExpect(conte);
    }

    @Test
    public void testSignUp() throws Exception {

        doAnswer(i -> {
            return null;
        }).when(rabbitAdmin).declareExchange(anyObject());

        String json = """
                {"username": "application",
                    "email": "a.vlad.v@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isString());

        User user = repository.findById("application").get();
        assertNull(user.getChangePasswordCode());
        assertNotNull(user.getPassword());
        assertEquals("0", String.valueOf(user.getBalance()));
        assertNotNull(user.getOnline());


    }

    @Test
    public void testSignUpWithExistUsername() throws Exception {

        doAnswer(i -> {
            return null;
        }).when(rabbitAdmin).declareExchange(anyObject());

        createTestPerson("application");
        String json = """
                {"username": "application",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadPassword() throws Exception {

        String json = """
                {"username": "application",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadUsername() throws Exception {

        String json = """
                {"username": "    ",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    /*@Test
    public void testSignUpWithBadUsername2() throws Exception {

        String json = """
                {"username": "appli cation",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }
*/
    @Test
    public void testSignUpWithoutEmail() throws Exception {

        String json = """
                {"username": "application",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBadEmail() throws Exception {

        String json = """
                {"username": "application",
                    "email": "a.vlad.c@ya.",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithBlankEmail() throws Exception {

        String json = """
                {"username": "application",
                    "email": "",
                    "firstName": "Владислав",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }

    @Test
    public void testSignUpWithoutFirstName() throws Exception {

        String json = """
                {"username": "application",
                    "email": "a.vlad.c@ya.ru",
                    "firstName": "",
                    "lastName": "Ананьев",
                    "password": "12345"}
                """;

        mockMvc.perform(post("https://localhost/api/signup")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
    }


    @Test
    public void testSignIn() throws Exception {

        doAnswer(i -> {
            return null;
        }).when(rabbitAdmin).declareQueue(anyObject());

        createTestPerson("application");

        String json = """
                {"username": "application",
                 "password": "12345"}
                      """;

        mockMvc.perform(post("https://localhost/api/signin")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isString());
    }


    @Test
    public void testSignInFail() throws Exception {

        createTestPerson("application");

        String json = """
                {"username": "application",
                 "password": "12346"}
                      """;

        mockMvc.perform(post("https://localhost/api/signin")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }


    private User createTestPerson(String name) {
        User person = new User(name, "a.vlad.v@ya.ru", "Vladislav", "Ananyev",
                passwordEncoder.encode("12345"));
        person.grantAuthority(Role.ROLE_USER);
        person.setRoles(new ArrayList<>(Collections.singleton(Role.ROLE_USER)));
        person.setPhotos(Collections.singletonList("https://localhost/img/default.jpg"));
        return repository.save(person);
    }

    private User createTestPersonWithoutSave(String name) {
        User person = new User(name, "a.vlad.v@ya.ru", "Vladislav", "Ananyev",
                passwordEncoder.encode("12345"));
        person.grantAuthority(Role.ROLE_USER);
        //return repository.save(person);
        return person;
    }

    /*@Test
    public void testGetFriends2() throws Exception {
        User person = new User("application", "a.vlad.v@ya.ru", "Vladislav", "Ananyev", "12345");
        Mockito.when(repository.save(Mockito.any())).thenReturn(person);

    }*/



/*    @MockBean
    private JWTUtil jwtUtil;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private UserDetailsService userDetailsService;*/

    /*@Test
    public void getUserById() throws Exception {
        mockMvc.perform(get("/api/test2"))
                .andExpect(status().is2xxSuccessful());
                //.andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }*/
}