package com.example.meetings.location.service;

import com.example.meetings.chat.model.domain.Dialog;
import com.example.meetings.location.model.Location;
import com.example.meetings.location.model.dto.LocationRequest;
import com.example.meetings.location.repository.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 * Сервис точек локации, привязанных к диалогам.
 */
@Service
public class LocationService {

    private final LocationRepository locationRepository;

    /**
     * Создаёт сервис с репозиторием локаций.
     *
     * @param locationRepository репозиторий локаций
     */
    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Возвращает все сохранённые локации.
     *
     * @return список всех локаций
     */
    public List<Location> findAll() {
        return locationRepository.findAll();
    }

    /**
     * Загружает локацию по идентификатору.
     *
     * @param locationId идентификатор локации
     * @return найденная локация
     */
    public Location findById(Long locationId) {
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "exception.location.not.found"));
    }

    /**
     * Сохраняет новую локацию и привязывает её к диалогу.
     *
     * @param request параметры локации: название, координаты, радиус и изображение
     * @param dialog  диалог, к которому привязывается локация
     * @return сохранённая локация
     */
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
