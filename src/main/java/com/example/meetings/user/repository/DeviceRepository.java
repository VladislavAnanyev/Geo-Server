package com.example.meetings.user.repository;


import com.example.meetings.user.model.domain.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository extends CrudRepository<Device, Long> {
    Device findByFcmToken(String fcmToken);
}
