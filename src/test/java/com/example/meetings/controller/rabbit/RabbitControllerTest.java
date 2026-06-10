package com.example.meetings.controller.rabbit;

import com.example.meetings.chat.controller.RabbitController;
import com.example.meetings.chat.model.domain.Message;
import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.common.utils.JWTUtil;
import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.example.meetings.user.repository.UserRepository;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.DIALOGS,
        SqlScripts.MESSAGES_1196,
        SqlScripts.DIALOG_UPDATE_1196
}, executionPhase = BEFORE_TEST_METHOD)
public class RabbitControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitController rabbitController;

    @Autowired
    private JWTUtil jwtTokenUtil;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void sendMessageTest() throws IOException {
        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {
                        "dialogId":  1196,
                        "content": "12345",
                        "uniqueCode": "123456789"
                    }
                }
                """;

        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            JSONObject jo = (JSONObject) obj;
            assertEquals("1234567890", jo.get("payload").toString());
            return null;
        }).when(rabbitTemplate).convertAndSend(any(String.class), any(Object.class));

        String token = jwtTokenUtil.generateToken(userRepository.findUserByUsername("user1").orElseThrow());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        Integer expectedMessageCount = messageRepository.findAll().size() + 1;
        rabbitController.sendMessageFromAMQPClient(message);
        Integer actualMessageCount = messageRepository.findAll().size();

        assertEquals(expectedMessageCount, actualMessageCount);
        assertTrue(messageRepository.findAll().stream()
                .anyMatch(m -> "12345".equals(m.getContent()) && m.getDialog().getDialogId().equals(1196L)));
    }

    @Test
    public void sendMessageDuplicateUniqueCodeIsIgnored() throws IOException {
        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {
                        "dialogId":  1196,
                        "content": "duplicate-test",
                        "uniqueCode": "duplicate-unique-code-1196"
                    }
                }
                """;

        doAnswer(i -> null).when(rabbitTemplate).convertAndSend(any(String.class), any(Object.class));

        String token = jwtTokenUtil.generateToken(userRepository.findUserByUsername("user1").orElseThrow());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message amqpMessage =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        int before = messageRepository.findAll().size();
        rabbitController.sendMessageFromAMQPClient(amqpMessage);
        int afterFirst = messageRepository.findAll().size();

        clearInvocations(rabbitTemplate);
        rabbitController.sendMessageFromAMQPClient(amqpMessage);

        assertEquals(before + 1, afterFirst);
        assertEquals(afterFirst, messageRepository.findAll().size());
        assertEquals(1, messageRepository.findAll().stream()
                .filter(m -> "duplicate-unique-code-1196".equals(m.getUniqueCode()))
                .count());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    public void sendMessageForbiddenTest() throws Exception {
        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {
                        "dialogId":  1196,
                        "content": "12345",
                        "uniqueCode": "123456789"
                    }
                }
                """;

        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            JSONObject jo = (JSONObject) obj;
            assertEquals("123456789", jo.get("uniqueCode"));
            return null;
        }).when(rabbitTemplate).convertAndSend(any(String.class), any(Object.class));

        Integer expectedMessageCount = messageRepository.findAll().size();

        String token = jwtTokenUtil.generateToken(userRepository.findUserByUsername("user3").orElseThrow());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        try {
            rabbitController.sendMessageFromAMQPClient(message);
            fail("Expected SecurityException");
        } catch (SecurityException | IOException e) {
            assertNotEquals("", e.getMessage());
        }

        Integer actualMessageCount = messageRepository.findAll().size();
        assertEquals(expectedMessageCount, actualMessageCount);
    }

    @Test
    public void sendMessageWithBlankContentTest() throws IOException {
        doAnswer(i -> null).when(rabbitTemplate).convertAndSend(any(String.class), any(Object.class));

        int before = messageRepository.findAll().size();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {
                        "dialogId":  1196,
                        "content": "",
                        "uniqueCode": "123456789"
                    }
                }
                """;

        String token = jwtTokenUtil.generateToken(userRepository.findUserByUsername("user1").orElseThrow());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        rabbitController.sendMessageFromAMQPClient(message);

        assertEquals(before + 1, messageRepository.findAll().size());
        assertTrue(messageRepository.findAll().stream()
                .anyMatch(m -> "".equals(m.getContent())));
    }

    @Test
    public void sendMessageWithNullContentTest() throws IOException {
        doAnswer(i -> null).when(rabbitTemplate).convertAndSend(any(String.class), any(Object.class));

        int before = messageRepository.findAll().size();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {
                        "dialogId":  1196,
                        "uniqueCode": "123456789"
                    }
                }
                """;

        String token = jwtTokenUtil.generateToken(userRepository.findUserByUsername("user1").orElseThrow());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message = new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);
        rabbitController.sendMessageFromAMQPClient(message);

        assertEquals(before + 1, messageRepository.findAll().size());
        assertTrue(messageRepository.findAll().stream()
                .anyMatch(m -> m.getContent() == null || m.getContent().isEmpty()));
    }
}
