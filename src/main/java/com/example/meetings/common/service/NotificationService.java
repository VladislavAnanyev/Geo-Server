package com.example.meetings.common.service;

import com.example.meetings.chat.model.domain.Notifiable;
import com.example.meetings.user.model.domain.Device;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Отправка push-уведомлений на FCM-токены устройств получателей.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final FirebaseMessaging fcm;

    /**
     * Отправляет push-уведомление с заголовком и текстом.
     *
     * @param title      заголовок уведомления
     * @param body       текст уведомления
     * @param notifiable объект с получателями и их устройствами
     */
    public void send(String title, String body, Notifiable notifiable) {
        send(
                Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build(),
                notifiable
        );
    }

    /**
     * Отправляет push-уведомление с заголовком, текстом и изображением.
     *
     * @param title      заголовок уведомления
     * @param body       текст уведомления
     * @param image      URL изображения
     * @param notifiable объект с получателями и их устройствами
     */
    public void send(String title, String body, String image, Notifiable notifiable) {
        send(
                Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .setImage(image)
                        .build(),
                notifiable
        );
    }

    private void send(Notification notification, Notifiable notifiable) {
        List<String> tokens = new ArrayList<>();
        notifiable.getUsersToSendNotification().forEach(
                user -> user.getDevices()
                        .stream()
                        .map(Device::getFcmToken)
                        .filter(Objects::nonNull)
                        .forEach(tokens::add)
        );

        try {
            fcm.sendEachForMulticast(
                    MulticastMessage.builder()
                            .addAllTokens(tokens)
                            .setNotification(notification)
                            .build()
            );
        } catch (Exception e) {
            log.error("Ошибка при отправке push уведомления", e);
        }
    }
}
