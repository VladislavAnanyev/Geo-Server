package com.example.mywebquizengine.user.repository;


import com.example.mywebquizengine.user.model.domain.Device;
import org.springframework.data.repository.CrudRepository;

public interface DeviceRepository extends CrudRepository<Device, Long> {
}
