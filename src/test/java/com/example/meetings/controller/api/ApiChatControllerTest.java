package com.example.meetings.controller.api;

import com.example.meetings.chat.model.LastDialogDTO;
import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.chat.model.domain.MessageStatus;
import com.example.meetings.chat.model.dto.input.EditMessageRequest;
import com.example.meetings.chat.model.dto.output.CreateDialogResponse;
import com.example.meetings.chat.model.dto.output.GetDialogsResponse;
import com.example.meetings.chat.repository.DialogRepository;
import com.example.meetings.chat.repository.MessageRepository;
import com.example.meetings.controller.config.AbstractControllerIntegrationTest;
import com.example.meetings.controller.config.SqlScripts;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Sql(scripts = {
        SqlScripts.CLEANUP,
        SqlScripts.USERS,
        SqlScripts.FRIENDS,
        SqlScripts.DIALOGS,
        SqlScripts.MESSAGES_1196,
        SqlScripts.MESSAGES_GROUP_1202,
        SqlScripts.MESSAGES_1224,
        SqlScripts.MESSAGES_1277,
        SqlScripts.DIALOG_UPDATE_1196,
        SqlScripts.DIALOG_UPDATE_1202,
        SqlScripts.DIALOG_UPDATE_1224,
        SqlScripts.DIALOG_UPDATE_1277
}, executionPhase = BEFORE_TEST_METHOD)
public class ApiChatControllerTest extends AbstractControllerIntegrationTest {

    @Autowired
    private DialogRepository dialogRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void getMessagesInNotExistDialog() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/" + 5555).secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user5", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetDialogList() throws Exception {

        mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.dialogs", hasSize(3)))
                .andExpect(jsonPath("$.result.dialogs[0].dialogId", instanceOf(Number.class)))
                .andExpect(jsonPath("$.result.dialogs[0].dialogId").value("1277"))
                .andExpect(jsonPath("$.result.dialogs[0].name").value("user4"))
                .andExpect(jsonPath("$.result.dialogs[0].content").value("f"))
                .andExpect(jsonPath("$.result.dialogs[*].dialogId", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].name", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].image", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].timestamp", notNullValue()))
                .andExpect(jsonPath("$.result.dialogs[*].lastSender", notNullValue()));
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user4", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user4", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
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
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetMessagesOnSecondPageInDialogForbidden() throws Exception {
        mockMvc.perform(get("/api/v1/dialog/1277?page=1").secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testGetDialogsReturnDialogsSortedByTimestamp() throws Exception {
        String json = mockMvc.perform(get("/api/v1/dialogs").secure(true))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        GetDialogsResponse getDialogsResponse = objectMapper.readValue(json, GetDialogsResponse.class);

        List<Date> datesFromRequest = new ArrayList<>();
        for (LastDialogDTO lastDialog : getDialogsResponse.getGetDialogsResult().getDialogs()) {
            datesFromRequest.add(lastDialog.getTimestamp());
        }

        List<Date> manualDates = new ArrayList<>(datesFromRequest);
        sort(manualDates);
        reverse(manualDates);

        assertEquals(datesFromRequest, manualDates);
    }


    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testEditMessage() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(message.getContent(), messageRepository.findById(1197L).get().getContent());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testEditMessageForbidden() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/1197").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithUserDetails(value = "user2", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testEditMessageNotFound() throws Exception {
        EditMessageRequest message = new EditMessageRequest();
        message.setContent("1234");

        mockMvc.perform(put("/api/v1/message/54321").secure(true)
                .content(objectMapper.writeValueAsString(message))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

    }


    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeleteMessage() throws Exception {
        mockMvc.perform(delete("/api/v1/message/1199").secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        assertEquals(MessageStatus.DELETED, messageRepository.findById(1199L).get().getStatus());

    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeleteMessageNotFound() throws Exception {
        mockMvc.perform(delete("/api/v1/message/54321").secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithUserDetails(value = "user1", userDetailsServiceBeanName = "authService", setupBefore = TestExecutionEvent.TEST_EXECUTION)
    public void testDeleteMessageForbidden() throws Exception {
        mockMvc.perform(delete("/api/v1/message/1198").secure(true))
                .andExpect(status().isForbidden());
    }


}
