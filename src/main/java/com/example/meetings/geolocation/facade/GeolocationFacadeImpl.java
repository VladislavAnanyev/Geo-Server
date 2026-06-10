package com.example.meetings.geolocation.facade;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.meeting.model.GeolocationDto;
import com.example.meetings.meeting.model.GeolocationModel;
import com.example.meetings.meeting.model.GetGeolocationsResult;
import com.example.meetings.photo.model.domain.Photo;
import com.example.meetings.user.model.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

/**
 * Реализация фасада геолокации.
 */
@Service
public class GeolocationFacadeImpl implements GeolocationFacade {

    @Value("${geolocation.recent-window-minutes:30}")
    private int recentWindowMinutes;

    @Value("${geolocation.people-nearby-radius-meters:1000}")
    private int peopleNearbyRadiusMeters;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private GeolocationMeetingAsyncService geolocationMeetingAsyncService;

    /**
     * Сохраняет геолокацию пользователя и асинхронно ищет встречи поблизости.
     *
     * @param userId           идентификатор пользователя
     * @param geolocationModel координаты и время обновления геолокации
     */
    @Override
    @Transactional
    public void addGeolocation(Long userId, GeolocationModel geolocationModel) {
        Geolocation geolocation = geolocationService.saveGeolocation(userId, geolocationModel);
        geolocationMeetingAsyncService.searchMeetings(
                userId,
                geolocation.getLat(),
                geolocation.getLng(),
                geolocation.getUpdatedAt()
        );
    }

    /**
     * Возвращает актуальные геолокации друзей пользователя.
     *
     * @param userId идентификатор пользователя
     * @return результат со списком геолокаций друзей
     */
    @Override
    public GetGeolocationsResult getFriendsGeolocations(Long userId) {
        List<Geolocation> geolocations = geolocationService.getFriendsGeolocations(userId);

        return new GetGeolocationsResult()
                .setItems(
                        geolocations.stream()
                                .map(this::toGeolocationDto)
                                .collect(toList())
                );
    }

    /**
     * Возвращает последнюю геолокацию пользователя, если она обновлялась в заданном временном окне.
     *
     * @param userId идентификатор пользователя
     * @return последняя геолокация пользователя или пустой результат, если актуальной геолокации нет
     */
    @Override
    public Optional<GeolocationDto> getRecentOwnGeolocation(Long userId) {
        return geolocationService.getRecentGeolocation(userId, recentWindowMinutes).map(this::toGeolocationDto);
    }

    /**
     * Возвращает пользователей в квадрате вокруг последней геолокации текущего пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список геолокаций людей поблизости; пустой список, если у пользователя нет актуальной геолокации
     */
    @Override
    public List<GeolocationDto> getPeopleInSquare(Long userId) {
        Optional<GeolocationDto> recent = geolocationService.getRecentGeolocation(userId, recentWindowMinutes).map(this::toGeolocationDto);
        if (recent.isEmpty()) {
            return List.of();
        }
        GeolocationDto centerDto = recent.get();
        if (centerDto.getLat() == null || centerDto.getLng() == null) {
            return List.of();
        }
        Geolocation center = new Geolocation().setLat(centerDto.getLat()).setLng(centerDto.getLng());
        return geolocationService.findInSquare(center, peopleNearbyRadiusMeters, LocalDateTime.now())
                .stream()
                .filter(g -> g.getUser() != null && !Objects.equals(g.getUser().getUserId(), userId))
                .map(this::toGeolocationDto)
                .collect(toList());
    }

    private GeolocationDto toGeolocationDto(Geolocation geolocation) {
        User user = geolocation.getUser();
        return new GeolocationDto()
                .setLat(geolocation.getLat())
                .setLng(geolocation.getLng())
                .setLastUpdateAt(geolocation.getUpdatedAt())
                .setUserId(user.getUserId())
                .setFirstName(user.getFirstName())
                .setUsername(user.getUsername())
                .setAvatar(Optional.ofNullable(user.getMainPhoto()).map(Photo::getUrl).orElse(null));
    }
}
