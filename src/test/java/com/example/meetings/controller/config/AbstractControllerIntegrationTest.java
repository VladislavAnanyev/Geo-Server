package com.example.meetings.controller.config;

import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.UserRepository;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.test.web.servlet.MockMvc;

import static com.example.meetings.util.Constants.ADMIN_ID;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@ContextConfiguration(initializers = AbstractControllerIntegrationTest.DockerInitializer.class)
public abstract class AbstractControllerIntegrationTest {

    @MockBean
    protected FirebaseApp firebaseApp;

    @MockBean
    protected FirebaseMessaging firebaseMessaging;

    @MockBean
    protected RabbitTemplate rabbitTemplate;

    @MockBean
    protected RabbitAdmin rabbitAdmin;

    @Autowired
    protected MockMvc mockMvc;

    protected void ensureSupportUserExists(UserRepository userRepository) {
        if (userRepository.findById(ADMIN_ID).isPresent()) {
            return;
        }
        User user = new User();
        user.setUserId(ADMIN_ID);
        user.setUsername("support");
        user.setFirstName("Support");
        user.setPassword("$2a$10$jPo1EgeMIRbhqKIBI.nDieIwEkAEfa6Qe.rze7gxUvdOeXAhnJrM6");
        user.setDescription("support");
        userRepository.save(user);
    }

    public static class DockerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(ConfigurableApplicationContext context) {
            DockerTestcontainers.ensureStarted();
            TestPropertyValues.of(
                    "spring.datasource.url=" + DockerTestcontainers.POSTGRES.getJdbcUrl(),
                    "spring.datasource.username=" + DockerTestcontainers.POSTGRES.getUsername(),
                    "spring.datasource.password=" + DockerTestcontainers.POSTGRES.getPassword(),
                    "spring.datasource.initialization-mode=never"
            ).applyTo(context.getEnvironment());
        }
    }
}
