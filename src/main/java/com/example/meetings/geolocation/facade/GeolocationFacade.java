package com.example.meetings.geolocation.facade;

import com.example.meetings.meeting.model.GeolocationDto;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;

import java.util.List;
import java.util.Optional;

/**
 * Фасад сценариев API геолокации.
 */
public interface GeolocationFacade {

    /**
     * Сохраняет геолокацию пользователя и асинхронно ищет встречи поблизости.
     *
     * @param userId           идентификатор пользователя
     * @param geolocationModel координаты и время обновления геолокации
     */
    void addGeolocation(Long userId, GeolocationModel geolocationModel);

    /**
     * Возвращает актуальные геолокации друзей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return результат со списком геолокаций друзей
     */
    GetGeolocationsResult getFriendsGeolocations(Long userId);

    /**
     * Возвращает последнюю геолокацию пользователя, если она обновлялась в заданном временном окне.
     *
     * @param userId идентификатор пользователя
     * @return последняя геолокация пользователя или пустой результат, если актуальной геолокации нет
     */
    Optional<GeolocationDto> getRecentOwnGeolocation(Long userId);

    /**
     * Возвращает пользователей в квадрате вокруг последней геолокации текущего пользователя.
     *
     * @param userId       идентификатор пользователя
     * @param radiusMeters радиус поиска в метрах; {@code null} — значение по умолчанию
     * @return список геолокаций людей поблизости; пустой список, если у пользователя нет актуальной геолокации
     */
    List<GeolocationDto> getPeopleInSquare(Long userId, Integer radiusMeters);
}
