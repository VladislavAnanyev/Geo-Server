package com.example.meetings.meeting.service;

import com.example.meetings.geolocation.model.Geolocation;
import com.example.meetings.geolocation.service.GeolocationService;
import com.example.meetings.util.Constants;
import com.example.meetings.common.exception.SubscriptionRequiredException;
import com.example.meetings.meeting.model.domain.Meeting;
import com.example.meetings.meeting.model.dto.output.MeetingView;
import com.example.meetings.meeting.repository.MeetingRepository;
import com.example.meetings.subscription.service.SubscriptionService;
import com.example.meetings.user.model.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static java.util.Objects.isNull;

/**
 * Сервис встреч пользователей на основе геолокации.
 */
@Service
@Slf4j
public class MeetingService {

    @Value("${geolocation.meetingsdistance}")
    private Integer meetingDistance;

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private GeolocationService geolocationService;

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * Возвращает встречи пользователя за указанный период.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата периода или {@code null} для текущего дня
     * @param endAt   конечная дата периода или {@code null} для текущего дня
     * @return список встреч пользователя
     */
    public List<MeetingView> getMyMeetings(Long userId, LocalDate startAt, LocalDate endAt) {
        startAt = isNull(startAt) ? LocalDate.now() : startAt;
        endAt = isNull(endAt) ? LocalDate.now() : endAt;

        return meetingRepository.getMyMeetings(userId, startAt.atStartOfDay(), endAt.plusDays(1).atStartOfDay());
    }

    /**
     * Для бесплатной подписки доступны встречи только за сегодня и вчера.
     *
     * @param userId  идентификатор пользователя
     * @param startAt начальная дата запрашиваемого периода или {@code null} для текущего дня
     * @param endAt   конечная дата запрашиваемого периода или {@code null} для текущего дня
     */
    public void validateMeetingHistoryAccess(Long userId, LocalDate startAt, LocalDate endAt) {
        startAt = isNull(startAt) ? LocalDate.now() : startAt;
        endAt = isNull(endAt) ? LocalDate.now() : endAt;

        if (subscriptionService.isPremium(userId)) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate freeStart = today.minusDays(1);

        if (startAt.isBefore(freeStart) || endAt.isAfter(today)) {
            throw new SubscriptionRequiredException(
                    "exception.subscription.meetings.history.limit",
                    Constants.ERROR_SUBSCRIPTION_MEETINGS_HISTORY_LIMIT
            );
        }
    }

    /**
     * Находит и создаёт новые встречи с пользователями, находящимися рядом в момент обновления геолокации.
     *
     * @param geolocation геолокация пользователя с координатами и временем обновления
     * @return список только что созданных встреч
     */
    public List<Meeting> findNowMeetings(Geolocation geolocation) {
        List<Geolocation> peopleNearMe = geolocationService.findInSquare(geolocation, meetingDistance, geolocation.getUpdatedAt())
                .stream()
                .filter(geo -> !geo.getUser().getUserId().equals(geolocation.getUser().getUserId()))
                .toList();

        List<Meeting> newMeetings = new ArrayList<>();
        for (Geolocation peopleGeolocation : peopleNearMe) {
            Meeting meeting = createMeeting(geolocation, peopleGeolocation);
            if (meeting != null) {
                newMeetings.add(meeting);
            }
        }

        return newMeetings;
    }

    /**
     * Создаёт встречу, если для пары пользователей в текущем окне её ещё нет.
     * В first/second всегда меньший и больший user id, чтобы unique (first, second, bucket) не зависел от порядка гео.
     *
     * @param firstGeolocation  геолокация первого пользователя
     * @param secondGeolocation геолокация второго пользователя
     * @return новая встреча или {@code null}, если она уже существовала
     */
    @Transactional
    public Meeting createMeeting(Geolocation firstGeolocation, Geolocation secondGeolocation) {
        User sender = firstGeolocation.getUser();
        User other = secondGeolocation.getUser();
        if (sender.getUserId().equals(other.getUserId())) {
            return null;
        }

        User firstUser = sender.getUserId() < other.getUserId() ? sender : other;
        User secondUser = sender.getUserId() < other.getUserId() ? other : sender;
        LocalDateTime updatedAt = firstGeolocation.getUpdatedAt();
        String bucket = meetingBucket(updatedAt);

        return meetingRepository.insertIfAbsent(
                firstGeolocation.getLat(),
                firstGeolocation.getLng(),
                updatedAt,
                firstUser.getUserId(),
                secondUser.getUserId(),
                bucket
        ).map(meetingId -> new Meeting()
                .setMeetingId(meetingId)
                .setFirstUser(firstUser)
                .setSecondUser(secondUser)
                .setLat(firstGeolocation.getLat())
                .setLng(firstGeolocation.getLng())
                .setTime(updatedAt)
                .setBucket(bucket)
        ).orElse(null);
    }

    /**
     * Ключ окна встречи. Сейчас — календарный день; смена механики меняет только эту формулу.
     */
    private String meetingBucket(LocalDateTime time) {
        return time.toLocalDate().toString();
    }

    /**
     * Загружает встречу по идентификатору.
     *
     * @param meetingId идентификатор встречи
     * @return найденная встреча
     */
    public Meeting findMeetingById(Long meetingId) {
        return meetingRepository.findById(meetingId).orElseThrow();
    }
}
