package com.example.meetings.geolocation.frequentplace;

import com.example.meetings.geolocation.model.GeolocationHistory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class FrequentPlaceGridClusterer {

    private static final double METERS_PER_DEGREE_LAT = 111_320d;
    private static final int EARTH_RADIUS_METERS = 6_371_210;

    public List<FrequentPlaceCluster> cluster(
            List<GeolocationHistory> history,
            int gridSizeMeters,
            int minPoints,
            int maxPlaces
    ) {
        Map<String, List<GeolocationHistory>> buckets = new HashMap<>();

        for (GeolocationHistory point : history) {
            if (point.getLat() == null || point.getLng() == null) {
                continue;
            }
            String gridKey = gridKey(point.getLat(), point.getLng(), gridSizeMeters);
            buckets.computeIfAbsent(gridKey, ignored -> new ArrayList<>()).add(point);
        }

        return buckets.entrySet().stream()
                .filter(entry -> entry.getValue().size() >= minPoints)
                .sorted(Comparator.<Map.Entry<String, List<GeolocationHistory>>>comparingInt(entry -> entry.getValue().size())
                        .reversed())
                .limit(maxPlaces)
                .map(entry -> toCluster(entry.getKey(), entry.getValue()))
                .toList();
    }

    private FrequentPlaceCluster toCluster(String gridKey, List<GeolocationHistory> points) {
        double latSum = 0;
        double lngSum = 0;
        for (GeolocationHistory point : points) {
            latSum += point.getLat();
            lngSum += point.getLng();
        }
        int count = points.size();
        GeolocationHistory medoid = findMedoid(points);

        return new FrequentPlaceCluster()
                .setGridKey(gridKey)
                .setLat(latSum / count)
                .setLng(lngSum / count)
                .setRepresentativeLat(medoid.getLat())
                .setRepresentativeLng(medoid.getLng())
                .setVisitCount(count);
    }

    /**
     * Точка из истории, суммарно ближе всего ко всем остальным визитам кластера.
     * Это реальный GPS-fix пользователя, а не усреднённый центроид.
     */
    private GeolocationHistory findMedoid(List<GeolocationHistory> points) {
        GeolocationHistory best = points.get(0);
        double bestScore = Double.MAX_VALUE;

        for (GeolocationHistory candidate : points) {
            double score = 0;
            for (GeolocationHistory other : points) {
                score += haversineMeters(
                        candidate.getLat(), candidate.getLng(),
                        other.getLat(), other.getLng()
                );
            }
            if (score < bestScore) {
                bestScore = score;
                best = candidate;
            }
        }
        return best;
    }

    private String gridKey(double lat, double lng, int gridSizeMeters) {
        double latStep = gridSizeMeters / METERS_PER_DEGREE_LAT;
        double lngStep = gridSizeMeters / (METERS_PER_DEGREE_LAT * Math.cos(Math.toRadians(lat)));
        long latCell = Math.round(lat / latStep);
        long lngCell = Math.round(lng / lngStep);
        return latCell + ":" + lngCell;
    }

    private static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(a));
    }
}
