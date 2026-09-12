package com.example.meetings.integration.overpass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.springframework.http.HttpMethod.POST;

import static com.example.meetings.util.Constants.EARTH_RADIUS_METERS;

@Service
@Slf4j
public class OverpassClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${frequent-places.overpass.url:https://overpass-api.de/api/interpreter}")
    private String overpassUrl;

    public OverpassClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public Optional<OverpassPoi> findNearestNamedPoi(double lat, double lng, int radiusMeters) {
        String overpassQuery = String.format(
                Locale.ROOT,
                "[out:json][timeout:15];"
                        + "(node(around:%d,%.6f,%.6f)[name];way(around:%d,%.6f,%.6f)[name];);"
                        + "out center tags 15;",
                radiusMeters, lat, lng, radiusMeters, lat, lng
        );

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("data", overpassQuery);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    overpassUrl,
                    POST,
                    new HttpEntity<>(body, headers),
                    String.class
            );
            if (response.getBody() == null || response.getBody().isBlank()) {
                return Optional.empty();
            }
            return parseNearestPoi(response.getBody(), lat, lng);
        } catch (Exception e) {
            log.warn("Overpass request failed for lat={}, lng={}: {}", lat, lng, e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<OverpassPoi> parseNearestPoi(String json, double targetLat, double targetLng) {
        try {
            JsonNode elements = objectMapper.readTree(json).path("elements");
            if (!elements.isArray() || elements.isEmpty()) {
                return Optional.empty();
            }

            Optional<PoiWithDistance> nearest = java.util.stream.StreamSupport.stream(elements.spliterator(), false)
                    .map(element -> toPoi(element, targetLat, targetLng))
                    .flatMap(Optional::stream)
                    .min(Comparator.comparingDouble(poi -> poi.distanceMeters));

            return nearest.map(PoiWithDistance::poi);
        } catch (Exception e) {
            log.warn("Failed to parse Overpass response: {}", e.getMessage());
            return Optional.empty();
        }
    }

    private Optional<PoiWithDistance> toPoi(JsonNode element, double targetLat, double targetLng) {
        JsonNode tagsNode = element.path("tags");
        if (!tagsNode.isObject()) {
            return Optional.empty();
        }
        String name = tagsNode.path("name").asText(null);
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }

        Double poiLat = null;
        Double poiLng = null;
        if (element.has("lat") && element.has("lon")) {
            poiLat = element.path("lat").asDouble();
            poiLng = element.path("lon").asDouble();
        } else if (element.has("center")) {
            poiLat = element.path("center").path("lat").asDouble();
            poiLng = element.path("center").path("lon").asDouble();
        }
        if (poiLat == null || poiLng == null) {
            return Optional.empty();
        }

        Map<String, String> tags = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = tagsNode.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            tags.put(field.getKey(), field.getValue().asText());
        }

        OverpassPoi poi = new OverpassPoi()
                .setRawName(name.trim())
                .setTags(tags)
                .setDisplayName(PlaceNameFormatter.format(name, tags));

        double distance = haversineMeters(targetLat, targetLng, poiLat, poiLng);
        return Optional.of(new PoiWithDistance(poi, distance));
    }

    private static double haversineMeters(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        return 2 * EARTH_RADIUS_METERS * Math.asin(Math.sqrt(a));
    }

    private record PoiWithDistance(OverpassPoi poi, double distanceMeters) {
    }
}
