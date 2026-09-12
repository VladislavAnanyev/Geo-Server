package com.example.meetings.auth.service;

import com.example.meetings.user.model.domain.Device;
import com.example.meetings.user.model.domain.User;
import com.example.meetings.user.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Регистрация FCM-устройств пользователя для push-уведомлений.
 */
@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    /**
     * Сохраняет FCM-токен устройства, если он ещё не привязан к пользователю.
     *
     * @param user     пользователь
     * @param fcmToken токен Firebase Cloud Messaging или {@code null}
     */
    public void processDevice(User user, String fcmToken) {
        if (fcmToken == null) {
            return;
        }

        boolean alreadyRegister = user.getDevices().stream().anyMatch(
                device -> fcmToken.equals(device.getFcmToken())
        );

        if (!alreadyRegister) {
            deviceRepository.save(
                    new Device()
                            .setFcmToken(fcmToken)
                            .setUser(user)
            );
        }
    }
}
