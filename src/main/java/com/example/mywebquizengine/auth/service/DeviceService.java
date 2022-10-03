package com.example.mywebquizengine.auth.service;

import com.example.mywebquizengine.user.model.domain.Device;
import com.example.mywebquizengine.user.model.domain.User;
import com.example.mywebquizengine.user.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    @Autowired
    private DeviceRepository deviceRepository;

    public void registerDevice(User user, String appleToken) {
        if (appleToken != null) {
            boolean alreadyRegister = user.getDevices().stream().anyMatch(
                    device -> appleToken.equals(device.getDeviceToken())
            );

            if (!alreadyRegister) {
                Device device = new Device();
                device.setDeviceToken(appleToken);
                device.setUser(user);
                deviceRepository.save(device);
            }
        }
    }
}
