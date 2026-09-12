package com.example.meetings.geolocation.repository;

import com.example.meetings.geolocation.model.Geolocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface GeolocationRepository extends CrudRepository<Geolocation, String>, JpaRepository<Geolocation, String> {

    /**
     * Получить текущие координаты указанных пользователей
     *
     * @param userIds список идентификаторов пользователей
     * @return текущие координаты указанных пользователей
     */
    @Query(value = """
            SELECT *
            FROM USERS JOIN GEOLOCATIONS G ON USERS.USER_ID = G.USER_ID
            WHERE G.USER_ID IN :userIds AND G.geolocation_id IN (select max(E.geolocation_id)
                                                                                          from GEOLOCATIONS E
                                                                                          GROUP BY E.user_id)
              AND NOT (
                  EXISTS (
                      SELECT 1
                      FROM GEOLOCATION_EXCLUDE_ZONES EZ
                      WHERE EZ.enabled = true
                        AND G.lat BETWEEN (
                            EZ.lat - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        ) AND (
                            EZ.lat + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        )
                        AND G.lng BETWEEN (
                            EZ.lng - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        ) AND (
                            EZ.lng + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        )
                  )
              )
            """, nativeQuery = true)
    List<Geolocation> getUsersGeolocations(List<Long> userIds);

    /**
     * Найти пользователей рядом
     *
     * @param lat
     * @param lng
     * @param aroundLat
     * @param aroundLng
     * @param userId
     * @param startAt
     * @return
     */
    @Query(value = """
            SELECT DISTINCT ON (user_id) *
            FROM GEOLOCATIONS
            WHERE LAT BETWEEN :lat - :aroundLat AND :lat + :aroundLat
              AND LNG BETWEEN :lng - :aroundLng AND :lng + :aroundLng
              AND UPDATED_AT BETWEEN :startAt AND :endAt
              AND NOT (
                  EXISTS (
                      SELECT 1
                      FROM GEOLOCATION_EXCLUDE_ZONES EZ
                      WHERE EZ.enabled = true
                        AND lat BETWEEN (
                            EZ.lat - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        ) AND (
                            EZ.lat + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        )
                        AND lng BETWEEN (
                            EZ.lng - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        ) AND (
                            EZ.lng + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        )
                  )
              )
            ORDER BY user_id, updated_at DESC NULLS LAST""", nativeQuery = true)
    List<Geolocation> findInSquare(Double lat, Double lng, Double aroundLat,
                                   Double aroundLng, LocalDateTime startAt, LocalDateTime endAt);

    @Query(value = """
            SELECT *
            FROM GEOLOCATIONS
            WHERE user_id = :userId
              AND NOT (
                  EXISTS (
                      SELECT 1
                      FROM GEOLOCATION_EXCLUDE_ZONES EZ
                      WHERE EZ.enabled = true
                        AND lat BETWEEN (
                            EZ.lat - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        ) AND (
                            EZ.lat + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0))
                        )
                        AND lng BETWEEN (
                            EZ.lng - (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        ) AND (
                            EZ.lng + (EZ.radius_meters / ((pi() / 180.0) * 6371210.0 * cos(radians(EZ.lat))))
                        )
                  )
              )
            ORDER BY updated_at DESC NULLS LAST
            LIMIT 1
            """, nativeQuery = true)
    Optional<Geolocation> findLatestByUserId(@Param("userId") Long userId);
}

