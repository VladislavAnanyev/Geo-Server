package com.example.meetings.location.service;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.location.model.Location;
import com.example.meetings.location.model.dto.LocationRequest;
import com.example.meetings.location.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    public Location save(LocationRequest request, Dialog dialog) {
        return locationRepository.save(
                new Location()
                        .setImage(request.getImage())
                        .setRadius(request.getRadius())
                        .setName(request.getName())
                        .setLng(request.getLng())
                        .setLat(request.getLat())
                        .setDialog(dialog)
        );
    }
}
