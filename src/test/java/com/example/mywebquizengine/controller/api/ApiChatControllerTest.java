package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.controller.rabbit.RabbitController;
import com.example.mywebquizengine.model.chat.Dialog;
import com.example.mywebquizengine.model.chat.Message;
import com.example.mywebquizengine.model.chat.MessageStatus;
import com.example.mywebquizengine.model.rabbit.RabbitMessage;
import com.example.mywebquizengine.repos.DialogRepository;
import com.example.mywebquizengine.repos.MessageRepository;
import com.example.mywebquizengine.repos.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.doubleThat;
import static org.mockito.Mockito.anyObject;
import static org.mockito.Mockito.doAnswer;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
@TestPropertySource(locations = "classpath:application-test.properties")
public class ApiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DialogRepository dialogRepository;

    @MockBean
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RabbitController rabbitController;

    @Test
    public void sendMessageTest() throws JsonProcessingException {

        String json = """
                {   "type": "MESSAGE",
                    "payload": { 
                        "dialog": {
                            "dialogId":  1196
                        },          
                        "content": "12345",            
                        "sender": {
                            "username": "user1"
                        },
                        "uniqueCode": 123456789
                    }          
                }
                """;

        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            JSONObject jo = (JSONObject) obj;
            /*System.out.println(jo.get("payload").toString());
            System.out.println("AAA");
            assertEquals("1234567890", jo.get("payload").toString());*/
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        System.out.println("AAA");
        RabbitMessage message = objectMapper.readValue(json, RabbitMessage.class);
        Integer expectedMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size() + 1;
        rabbitController.sendMessageFromAMQPClient(message);
        Integer actualMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();

        assertEquals(expectedMessageCount, actualMessageCount);

    }

    @Test
    public void sendMessageForbiddenTest() throws JsonProcessingException, ParseException {

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": { 
                        "dialog": {
                            "dialogId":  1196
                        },          
                        "content": "12345",            
                        "sender": {
                            "username": "user3"
                        },
                        "uniqueCode": 123456789    
                    }      
                }
                """;

        RabbitMessage message = objectMapper.readValue(json, RabbitMessage.class);

        doAnswer(i -> {
            Object obj = new JSONParser().parse(i.getArgument(1).toString());
            /*JSONObject jo = (JSONObject) obj;
            assertEquals("123456789", jo.get("uniqueCode"));*/
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        Integer expectedMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();


        Authentication authentication = new UsernamePasswordAuthenticationToken(userRepository.findById("user3").get(), null);
        rabbitController.sendMessage(message, authentication);

        Integer actualMessageCount = ((ArrayList<Message>) messageRepository.findAll()).size();
        assertEquals(expectedMessageCount, actualMessageCount);

    }


    @Test
    public void sendMessageWithBlankContentTest() throws JsonProcessingException, ParseException {

        doAnswer(i -> {
            assertNotNull(i.getArgument(1).toString());
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        ArrayList<Message> expectedMessages = (ArrayList<Message>) messageRepository.findAll();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": { 
                        "dialog": {
                            "dialogId":  1196
                        },          
                        "content": "",            
                        "sender": {"username": "user1"},
                        "uniqueCode": 123456789
                    }          
                }
                """;

        RabbitMessage message = objectMapper.readValue(json, RabbitMessage.class);

        try {
            rabbitController.sendMessageFromAMQPClient(message);
            fail("Expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
            assertNotEquals("", e.getMessage());
        }

        ArrayList<Message> actualMessages = (ArrayList<Message>) messageRepository.findAll();

        assertEquals(expectedMessages.size(), actualMessages.size());

    }

    @Test
    public void sendMessageWithNullContentTest() throws JsonProcessingException, ParseException {

        doAnswer(i -> {
            assertNotNull(i.getArgument(1).toString());
            return null;
        }).when(rabbitTemplate).convertAndSend(anyString(), (Object) anyObject());

        ArrayList<Message> expectedMessages = (ArrayList<Message>) messageRepository.findAll();

        String json = """
                {
                    "type": "MESSAGE",
                    "payload": { 
                        "dialog": {
                            "dialogId":  1196
                        },                    
                        "sender": {"username": "user1"},
                        "uniqueCode": 123456789
                    }          
                }
                """;

        RabbitMessage message = objectMapper.readValue(json, RabbitMessage.class);

        try {
            rabbitController.sendMessageFromAMQPClient(message);
            fail("Expected ConstraintViolationException");
        } catch (ConstraintViolationException e) {
            assertNotEquals("", e.getMessage());
        }

        ArrayList<Message> actualMessages = (ArrayList<Message>) messageRepository.findAll();

        assertEquals(expectedMessages.size(), actualMessages.size());

    }

    @Test
    @WithUserDetails(value = "user1")
    public void getMessagesInNotExistDialog() throws Exception {
        mockMvc.perform(get("/api/messages?dialogId=" + 5555).secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user5")
    public void testGetEmptyDialogList() throws Exception {
        mockMvc.perform(get("/api/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }


    @Test
    public void testGetEmptyDialogWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/dialogs").secure(true))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = "user1")
    public void testGetDialogList() throws Exception {

        mockMvc.perform(get("/api/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].dialogId", instanceOf(Number.class)))
                .andExpect(jsonPath("$[0].dialogId").value("1277"))
                .andExpect(jsonPath("$[0].name").value("user4"))
                .andExpect(jsonPath("$[0].content").value("f"))
                .andExpect(jsonPath("$[0].content").value("f"))
                .andExpect(jsonPath("$[*].dialogId", notNullValue()))
                .andExpect(jsonPath("$[*].name", notNullValue()))
                .andExpect(jsonPath("$[*].image", notNullValue()))
                .andExpect(jsonPath("$[*].timestamp", notNullValue()))
                .andExpect(jsonPath("$[*].lastSender", notNullValue()));
    }


    @Test
    @WithUserDetails(value = "user1")
    public void testCreateDialog() throws Exception {

        String dialogId = mockMvc.perform(get("/api/getDialogId?username=user4")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber()).andReturn().getResponse().getContentAsString();

        Optional<Dialog> dialog = dialogRepository.findById(Long.valueOf(dialogId));

        assertTrue(dialog.isPresent());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testCreateExistDialog() throws Exception {

        String existDialogId = mockMvc.perform(get("/api/getDialogId?username=user4")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber()).andReturn().getResponse().getContentAsString();

        assertTrue(dialogRepository.findById(Long.valueOf(existDialogId)).isPresent());

        String newDialogId = mockMvc.perform(get("/api/getDialogId?username=user4")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber()).andReturn().getResponse().getContentAsString();

        assertEquals(existDialogId, newDialogId);
    }


    @Test
    @WithUserDetails(value = "user1")
    public void getMessagesInEmptyDialog() throws Exception {

        String dialogId = mockMvc.perform(get("/api/getDialogId?username=user5")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNumber()).andReturn().getResponse().getContentAsString();


        Optional<Dialog> dialog = dialogRepository.findById(Long.valueOf(dialogId));

        assertTrue(dialog.isPresent());

        mockMvc.perform(get("/api/messages?dialogId=" + dialogId).secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(0)));
    }


    @Test
    @WithUserDetails(value = "user4")
    public void testGetMessagesInBigDialog() throws Exception {
        mockMvc.perform(get("/api/messages?dialogId=1277").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(50)))
                .andExpect(jsonPath("$.messages[0].id").value("1324"))
                .andExpect(jsonPath("$.messages[0].content").value("asdf"))
                .andExpect(jsonPath("$.messages[49].id").value("1373"))
                .andExpect(jsonPath("$.messages[49].content").value("f"));
    }

    @Test
    @WithUserDetails(value = "user4")
    public void testGetMessagesOnSecondPageInDialog() throws Exception {
        mockMvc.perform(get("/api/messages?dialogId=1277&page=1").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages", hasSize(46)))
                .andExpect(jsonPath("$.messages[0].id").value("1278"))
                .andExpect(jsonPath("$.messages[0].content").value("1"))
                .andExpect(jsonPath("$.messages[45].id").value("1323"))
                .andExpect(jsonPath("$.messages[45].content").value("sdf"));
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testGetMessagesOnSecondPageInDialogForbidden() throws Exception {
        mockMvc.perform(get("/api/messages?dialogId=1277&page=1").secure(true))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = "user1")
    public void testGetDialogsReturnDialogsSortedByTimestamp() throws Exception {
        String json = mockMvc.perform(get("/api/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].timestamp").isArray())
                .andReturn().getResponse().getContentAsString();

        List<Message> messages = objectMapper.readValue(json, new TypeReference<>() {
        });

        List<Date> datesFromRequest = new ArrayList<>();
        for (Message message : messages) {
            datesFromRequest.add(message.getTimestamp());
        }

        List<Date> manualDates = new ArrayList<>(datesFromRequest);
        sort(manualDates);
        reverse(manualDates);

        assertEquals(datesFromRequest, manualDates);
    }


    @Test
    @WithUserDetails(value = "user1")
    public void testEditMessage() throws Exception {

        Message message = new Message();
        message.setContent("1234");
        message.setId(1197L);

        mockMvc.perform(put("/api/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(message.getContent(), messageRepository.findById(1197L).get().getContent());

    }

    @Test
    @WithUserDetails(value = "user2")
    public void testEditMessageForbidden() throws Exception {

        Message message = new Message();
        message.setContent("1234");
        message.setId(1197L);

        mockMvc.perform(put("/api/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

    }

    @Test
    @WithUserDetails(value = "user2")
    public void testEditMessageNotFound() throws Exception {

        Message message = new Message();
        message.setContent("1234");
        message.setId(54321L);

        mockMvc.perform(put("/api/message/54321").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessage() throws Exception {

        mockMvc.perform(delete("/api/message/1199").secure(true))
                .andExpect(status().isOk());

        assertEquals(MessageStatus.DELETED, messageRepository.findById(1199L).get().getStatus());

    }

    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessageNotFound() throws Exception {
        mockMvc.perform(delete("/api/message/54321").secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessageForbidden() throws Exception {
        mockMvc.perform(delete("/api/message/1198").secure(true))
                .andExpect(status().isForbidden());
    }


}