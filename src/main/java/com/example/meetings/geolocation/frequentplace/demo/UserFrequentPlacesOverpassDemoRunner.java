package com.example.meetings.geolocation.frequentplace.demo;

import com.example.meetings.geolocation.frequentplace.FrequentPlaceCluster;
import com.example.meetings.geolocation.frequentplace.FrequentPlaceGridClusterer;
import com.example.meetings.geolocation.model.GeolocationHistory;
import com.example.meetings.geolocation.repository.GeolocationHistoryRepository;
import com.example.meetings.integration.overpass.OverpassClient;
import com.example.meetings.integration.overpass.OverpassPoi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "frequent-places.overpass-demo.enabled", havingValue = "true")
@Slf4j
public class UserFrequentPlacesOverpassDemoRunner implements ApplicationRunner {

    private final GeolocationHistoryRepository geolocationHistoryRepository;
    private final FrequentPlaceGridClusterer frequentPlaceGridClusterer;
    private final OverpassClient overpassClient;

    @Value("${frequent-places.overpass-demo.user-id:7}")
    private long userId;

    @Value("${frequent-places.overpass-demo.lookback-days:30}")
    private int lookbackDays;

    @Value("${frequent-places.overpass-demo.grid-size-meters:200}")
    private int gridSizeMeters;

    @Value("${frequent-places.overpass-demo.min-points:3}")
    private int minPoints;

    @Value("${frequent-places.overpass-demo.max-places:5}")
    private int maxPlaces;

    @Value("${frequent-places.overpass-demo.radius-meters:200}")
    private int radiusMeters;

    @Value("${frequent-places.overpass-demo.request-delay-ms:2000}")
    private long requestDelayMs;

    public UserFrequentPlacesOverpassDemoRunner(
            GeolocationHistoryRepository geolocationHistoryRepository,
            FrequentPlaceGridClusterer frequentPlaceGridClusterer,
            OverpassClient overpassClient
    ) {
        this.geolocationHistoryRepository = geolocationHistoryRepository;
        this.frequentPlaceGridClusterer = frequentPlaceGridClusterer;
        this.overpassClient = overpassClient;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        LocalDateTime since = LocalDateTime.now().minusDays(lookbackDays);
        List<GeolocationHistory> history = geolocationHistoryRepository
                .findByUser_UserIdAndCreatedAtAfterOrderByCreatedAtDesc(userId, since);

        log.info("=== Частые места пользователя {} (Overpass demo) ===", userId);
        log.info("Точек в истории за {} дней: {}", lookbackDays, history.size());

        if (history.isEmpty()) {
            log.info("История геолокации пуста — нечего кластеризовать.");
            return;
        }

        List<FrequentPlaceCluster> clusters = frequentPlaceGridClusterer.cluster(
                history, gridSizeMeters, minPoints, maxPlaces
        );

        if (clusters.isEmpty()) {
            log.info("Кластеров с минимум {} точками не найдено.", minPoints);
            return;
        }

        int placeNumber = 1;
        for (FrequentPlaceCluster cluster : clusters) {
            Optional<OverpassPoi> poi = overpassClient.findNearestNamedPoi(
                    cluster.getRepresentativeLat(),
                    cluster.getRepresentativeLng(),
                    radiusMeters
            );

            if (poi.isPresent()) {
                OverpassPoi resolved = poi.get();
                log.info(
                        "#{} visits={} grid={} query_lat={} query_lng={} centroid_lat={} centroid_lng={} poi=\"{}\" raw=\"{}\"",
                        placeNumber,
                        cluster.getVisitCount(),
                        cluster.getGridKey(),
                        formatCoord(cluster.getRepresentativeLat()),
                        formatCoord(cluster.getRepresentativeLng()),
                        formatCoord(cluster.getLat()),
                        formatCoord(cluster.getLng()),
                        resolved.getDisplayName(),
                        resolved.getRawName()
                );
            } else {
                log.info(
                        "#{} visits={} grid={} query_lat={} query_lng={} centroid_lat={} centroid_lng={} poi=<не найден в OSM>",
                        placeNumber,
                        cluster.getVisitCount(),
                        cluster.getGridKey(),
                        formatCoord(cluster.getRepresentativeLat()),
                        formatCoord(cluster.getRepresentativeLng()),
                        formatCoord(cluster.getLat()),
                        formatCoord(cluster.getLng())
                );
            }

            placeNumber++;
            if (placeNumber <= clusters.size() && requestDelayMs > 0) {
                Thread.sleep(requestDelayMs);
            }
        }

        log.info("=== Конец Overpass demo ===");
    }

    private static String formatCoord(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }
}
