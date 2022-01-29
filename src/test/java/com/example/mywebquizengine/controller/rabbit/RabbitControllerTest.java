/*
package com.example.mywebquizengine.controller.rabbit;

import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.repos.MessageRepository;
import com.example.mywebquizengine.repos.UserRepository;
import com.example.mywebquizengine.service.JWTUtil;
import com.rabbitmq.client.Channel;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyObject;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class RabbitControllerTest {

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RabbitController rabbitController;

    @Autowired
    private JWTUtil jwtTokenUtil;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void sendMessageTest() throws IOException, IllegalAccessException, NoSuchAlgorithmException {

        String json = """
                {   
                    "type": "MESSAGE",
                    "payload": { 
                        "dialogId":  1196,         
                        "content": "12345",            
                        "username": "user1",
                        "uniqueCode": "123456789"
                    }          
                }
                """;

        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            JSONObject jo = (JSONObject) obj;
            */
/*System.out.println(jo.get("payload").toString());
            System.out.println("AAA");
            assertEquals("1234567890", jo.get("payload").toString());*//*

            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());


        String token = jwtTokenUtil.generateToken(userRepository.findById("user1").get());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        Integer expectedMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size() + 1;
        rabbitController.sendMessageFromAMQPClient(message, Mockito.mock(Channel.class), 1L);
        Integer actualMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();

        assertEquals(expectedMessageCount, actualMessageCount);

    }

    @Test
    public void sendMessageForbiddenTest() {

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": { 
                        "dialogId":  1196,        
                        "content": "12345",            
                        "username": "user3",
                        "uniqueCode": "123456789"
                    }      
                }
                """;


        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            */
/*JSONObject jo = (JSONObject) obj;
            assertEquals("123456789", jo.get("uniqueCode"));*//*

            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        Integer expectedMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();

        String token = jwtTokenUtil.generateToken(userRepository.findById("user3").get());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);

        try {
            rabbitController.sendMessageFromAMQPClient(message, Mockito.mock(Channel.class), 1L);
            fail("Expected SecurityException");
        } catch (SecurityException | IOException | NoSuchAlgorithmException | IllegalAccessException e) {
            assertNotEquals("", e.getMessage());
        }

        Integer actualMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();
        assertEquals(expectedMessageCount, actualMessageCount);

    }


    @Test
    public void sendMessageWithBlankContentTest() {

        doAnswer(i -> {
            assertNotNull(i.getArgument(1).toString());
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        ArrayList<Message> expectedMessages = (ArrayList<Message>) messageRepository.findAll();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": {           
                        "dialogId":  1196,        
                        "content": "",            
                        "username": "user1",
                        "uniqueCode": "123456789"
                    }          
                }
                """;

        String token = jwtTokenUtil.generateToken(userRepository.findById("user1").get());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message =
                new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);


        try {
            rabbitController.sendMessageFromAMQPClient(message, Mockito.mock(Channel.class), 1L);
            fail("Expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
            assertNotEquals("", e.getMessage());
        } catch (IOException | IllegalAccessException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ArrayList<Message> actualMessages = (ArrayList<Message>) messageRepository.findAll();

        assertEquals(expectedMessages.size(), actualMessages.size());

    }

    @Test
    public void sendMessageWithNullContentTest() {

        doAnswer(i -> {
            assertNotNull(i.getArgument(1).toString());
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        ArrayList<Message> expectedMessages = (ArrayList<Message>) messageRepository.findAll();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": { 
                        "dialogId":  1196,                  
                        "uniqueCode": "123456789"
                    }          
                }
                """;

        String token = jwtTokenUtil.generateToken(userRepository.findById("user1").get());
        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("Authorization", token);

        org.springframework.amqp.core.Message message = new org.springframework.amqp.core.Message(json.getBytes(), messageProperties);
        try {
            rabbitController.sendMessageFromAMQPClient(message, Mockito.mock(Channel.class), 1L);
            fail("Expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
            assertNotEquals("", e.getMessage());
        } catch (IOException | IllegalAccessException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        ArrayList<Message> actualMessages = (ArrayList<Message>) messageRepository.findAll();

        assertEquals(expectedMessages.size(), actualMessages.size());

    }
}*/