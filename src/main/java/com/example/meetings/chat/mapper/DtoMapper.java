package com.example.meetings.chat.mapper;

import com.example.meetings.chat.model.*;
import com.example.meetings.chat.model.domain.*;
import com.example.meetings.chat.model.dto.input.Typing;
import com.example.meetings.user.model.domain.User;

import java.util.ArrayList;
import java.util.List;

import static com.example.meetings.chat.model.domain.MessageStatus.READ;
import static java.util.stream.Collectors.toList;

public class DtoMapper {
    public static UserDto map(User user) {
        return new UserDto()
                .setUserId(user.getUserId())
                .setFirstName(user.getFirstName())
                .setAvatar(user.getMainPhoto().getUrl())
                .setLastName(user.getLastName())
                .setOnline(user.isOnline())
                .setLogin(user.getUsername());
    }

    public static ChangeMessageStatusEventDto map(ChangeMessageStatusEvent event) {
        return new ChangeMessageStatusEventDto()
                .setDialogId(event.getDialog().getDialogId())
                .setStatus(event.getStatus());
    }

    public static TypingDto map(Typing typing) {
        return new TypingDto()
                .setDialogId(typing.getDialog().getDialogId())
                .setUsername(typing.getUser().getUsername())
                .setFirstName(typing.getUser().getFirstName())
                .setLastName(typing.getUser().getLastName())
                .setUserId(typing.getUser().getUserId());
    }

    public static MessageDTO map(Message message) {
        User sender = message.getSender();

        return new MessageDTO()
                .setDialogId(message.getDialog().getDialogId())
                .setUniqueCode(message.getUniqueCode())
                .setType(message.getType())
                .setTimestamp(message.getTimestamp())
                .setContent(message.getContent())
                .setAlreadyReadUserIds(getWhoAlreadyRead(message.getMessageStatusHistoryList()))
                .setStatus(message.getStatus().toString())
                .setSender(DtoMapper.map(sender))
                .setFiles(getFiles(message.getFiles()))
                .setMessageId(message.getMessageId())
                .setForwardedMessages(getMessages(message.getForwardedMessages()));
    }

    public static List<MessageFileDTO> getFiles(List<MessageFile> files) {
        List<MessageFileDTO> dtos = new ArrayList<>();
        if (files != null) {
            dtos = files.stream().map(file -> new MessageFileDTO()
                    .setContentType(file.getContentType())
                    .setOriginalName(file.getOriginalName())
                    .setUri(file.getUri())).collect(toList());
        }

        return dtos;
    }

    public static List<MessageDTO> getMessages(List<Message> messages) {
        List<MessageDTO> dtos = new ArrayList<>();
        if (messages != null) {
            dtos = messages.stream().map(DtoMapper::map).collect(toList());
        }
        return dtos;
    }

    public static List<Long> getWhoAlreadyRead(List<MessageStatusHistory> historyViews) {
        return historyViews
                .stream()
                .filter(messageStatusHistory -> messageStatusHistory.getMessageStatus().equals(READ))
                .map(messageStatusHistory -> messageStatusHistory.getUser().getUserId())
                .collect(toList());
    }
}

