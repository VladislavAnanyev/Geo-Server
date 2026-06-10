package com.example.meetings.integration.overpass;

import java.util.Map;

public final class PlaceNameFormatter {

    private PlaceNameFormatter() {
    }

    public static String format(String name, Map<String, String> tags) {
        if (name == null || name.isBlank()) {
            return null;
        }
        String trimmed = name.trim();
        if (tags == null || tags.isEmpty()) {
            return trimmed;
        }
        String leisure = tags.get("leisure");
        if ("stadium".equals(leisure)) {
            return "Стадион «" + trimmed + "»";
        }
        String amenity = tags.get("amenity");
        if ("cafe".equals(amenity)) {
            return "Кафе «" + trimmed + "»";
        }
        if ("restaurant".equals(amenity) || "fast_food".equals(amenity)) {
            return "Ресторан «" + trimmed + "»";
        }
        if ("bar".equals(amenity) || "pub".equals(amenity)) {
            return "Бар «" + trimmed + "»";
        }
        if ("pharmacy".equals(amenity)) {
            return "Аптека «" + trimmed + "»";
        }
        if ("hospital".equals(amenity) || "clinic".equals(amenity)) {
            return "Клиника «" + trimmed + "»";
        }
        if (amenity != null) {
            return trimmed;
        }
        String shop = tags.get("shop");
        if (shop != null) {
            return "Магазин «" + trimmed + "»";
        }
        String tourism = tags.get("tourism");
        if ("museum".equals(tourism)) {
            return "Музей «" + trimmed + "»";
        }
        if ("hotel".equals(tourism)) {
            return "Отель «" + trimmed + "»";
        }
        return trimmed;
    }
}
