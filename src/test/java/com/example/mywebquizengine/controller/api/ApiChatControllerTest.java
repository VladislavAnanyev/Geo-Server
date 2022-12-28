package com.example.mywebquizengine.controller.api;

import com.example.mywebquizengine.chat.model.domain.Dialog;
import com.example.mywebquizengine.chat.model.domain.Message;
import com.example.mywebquizengine.chat.model.domain.MessageStatus;
import com.example.mywebquizengine.chat.model.dto.input.EditMessageRequest;
import com.example.mywebquizengine.chat.model.dto.output.CreateDialogResponse;
import com.example.mywebquizengine.chat.model.dto.output.GetDialogsResponse;
import com.example.mywebquizengine.chat.model.dto.output.LastDialog;
import com.example.mywebquizengine.chat.repository.DialogRepository;
import com.example.mywebquizengine.chat.repository.MessageRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.lang.Long.valueOf;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test"})
public class ApiChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = "user1")
    public void getMessagesInNotExistDialog() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/" + 5555).secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user5")
    public void testGetEmptyDialogList() throws Exception {
        mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dialogs", hasSize(1)));
    }

    @Test
    public void testGetEmptyDialogWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isForbidden());
    }


    @Test
    @WithUserDetails(value = "user1")
    public void testGetDialogList() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dialogs", hasSize(3)))
                .andExpect(jsonPath("$.result.dialogs[0].dialogId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.result.dialogs[0].dialogId").value("1277"))
                .andExpect(jsonPath("$.result.dialogs[0].name").value("user4"))
                .andExpect(jsonPath("$.result.dialogs[0].content").value("f"))
                .andExpect(jsonPath("$.result.dialogs[0].content").value("f"))
                .andExpect(jsonPath("$.result.dialogs[*].dialogId", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].name", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].image", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].timestamp", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].lastSender", notNullValue()));
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testCreateDialog() throws Exception {
        String json = mockMvc.perform(post("/api/v1/dialog/create?userId=1004")
                .secure(true))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        CreateDialogResponse createDialogResponse = objectMapper.readValue(json, CreateDialogResponse.class);

        Optional<Dialog> dialog = dialogRepository.findById(createDialogResponse.getCreateDialogResult().getDialogId());
        assertTrue(dialog.isPresent());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testCreateExistDialog() throws Exception {
        String json1 = mockMvc.perform(post("/api/v1/dialog/create?userId=1004")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dialogId").isNumber()).andReturn().getResponse().getContentAsString();
        CreateDialogResponse createDialogResponse1 = objectMapper.readValue(json1, CreateDialogResponse.class);
        assertTrue(dialogRepository.findById(createDialogResponse1.getCreateDialogResult().getDialogId()).isPresent());

        String json2 = mockMvc.perform(post("/api/v1/dialog/create?userId=1004")
                .secure(true))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        CreateDialogResponse createDialogResponse2 = objectMapper.readValue(json2, CreateDialogResponse.class);
        assertEquals(
                createDialogResponse1.getCreateDialogResult().getDialogId(),
                createDialogResponse2.getCreateDialogResult().getDialogId()
        );
    }


    @Test
    @WithUserDetails(value = "user1")
    public void getMessagesInEmptyDialog() throws Exception {
        String json = mockMvc.perform(post("/api/v1/dialog/create?userId=1005")
                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dialogId").isNumber()).andReturn().getResponse().getContentAsString();

        CreateDialogResponse createDialogResponse = objectMapper.readValue(json, CreateDialogResponse.class);

        Long dialogId = createDialogResponse.getCreateDialogResult().getDialogId();
        Optional<Dialog> dialog = dialogRepository.findById(dialogId);

        assertTrue(dialog.isPresent());

        mockMvc.perform(get("/api/v1/dialog/" + dialogId).secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.messages", hasSize(0)));
    }


    @Test
    @WithUserDetails(value = "user4")
    public void testGetMessagesInBigDialog() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/1277").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.messages", hasSize(50)))
                .andExpect(jsonPath("$.result.messages[0].messageId").value("1324"))
                .andExpect(jsonPath("$.result.messages[0].content").value("asdf"))
                .andExpect(jsonPath("$.result.messages[49].messageId").value("1373"))
                .andExpect(jsonPath("$.result.messages[49].content").value("f"));
    }

    @Test
    @WithUserDetails(value = "user4")
    public void testGetMessagesOnSecondPageInDialog() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/1277?page=1").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.messages", hasSize(46)))
                .andExpect(jsonPath("$.result.messages[0].messageId").value("1278"))
                .andExpect(jsonPath("$.result.messages[0].content").value("1"))
                .andExpect(jsonPath("$.result.messages[45].messageId").value("1323"))
                .andExpect(jsonPath("$.result.messages[45].content").value("sdf"));
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testGetMessagesOnSecondPageInDialogForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/1277?page=1").secure(true))
                .andExpect(status().isForbidden());
    }

    //todo включить тест когда будет осуществлен переход от интерфейс-проекций к классам
/*    @Test
    @WithUserDetails(value = "user1")
    public void testGetDialogsReturnDialogsSortedByTimestamp() throws Exception {
        String json = mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GetDialogsResponse getDialogsResponse = objectMapper.readValue(json, GetDialogsResponse.class);

        List<Date> datesFromRequest = new ArrayList<>();
        for (LastDialog lastDialog : getDialogsResponse.getGetDialogsResult().getDialogs()) {
            datesFromRequest.add(lastDialog.getTimestamp());
        }

        List<Date> manualDates = new ArrayList<>(datesFromRequest);
        sort(manualDates);
        reverse(manualDates);

        assertEquals(datesFromRequest, manualDates);
    }*/


    @Test
    @WithUserDetails(value = "user1")
    public void testEditMessage() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        assertEquals(message.getContent(), messageRepository.findById(1197L).get().getContent());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testEditMessageForbidden() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2")
    public void testEditMessageNotFound() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/54321").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessage() throws Exception {
        mockMvc.perform(delete("/api/v1/message/1199").secure(true))
                .andExpect(status().isOk());

        assertEquals(MessageStatus.DELETED, messageRepository.findById(1199L).get().getStatus());

    }

    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessageNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/message/54321").secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user1")
    public void testDeleteMessageForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/message/1198").secure(true))
                .andExpect(status().isForbidden());
    }


}