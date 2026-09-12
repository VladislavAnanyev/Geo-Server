package com.example.meetings.geolocation.frequentplace;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(chain = true)
public class FrequentPlaceCluster {

    private String gridKey;
    /** Среднее координат кластера — для отображения. */
    private double lat;
    private double lng;
    /** Реальная точка из истории (medoid) — для запроса в Overpass. */
    private double representativeLat;
    private double representativeLng;
    private int visitCount;

    public FrequentPlaceCluster setGridKey(String gridKey) {
        this.gridKey = gridKey;
        return this;
    }

    public FrequentPlaceCluster setLat(double lat) {
        this.lat = lat;
        return this;
    }

    public FrequentPlaceCluster setLng(double lng) {
        this.lng = lng;
        return this;
    }

    public FrequentPlaceCluster setRepresentativeLat(double representativeLat) {
        this.representativeLat = representativeLat;
        return this;
    }

    public FrequentPlaceCluster setRepresentativeLng(double representativeLng) {
        this.representativeLng = representativeLng;
        return this;
    }

    public FrequentPlaceCluster setVisitCount(int visitCount) {
        this.visitCount = visitCount;
        return this;
    }
}
