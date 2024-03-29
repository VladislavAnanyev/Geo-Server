package com.example.meetings.common.rabbit.eventhandler;

import com.example.meetings.chat.facade.MessageFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommonEventHandler {
    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected MessageFacade messageFacade;
}
