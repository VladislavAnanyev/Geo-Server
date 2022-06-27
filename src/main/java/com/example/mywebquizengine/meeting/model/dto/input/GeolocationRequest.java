package com.example.mywebquizengine.meeting.model.dto.input;

import javax.validation.constraints.NotNull;

public class GeolocationRequest {

    @NotNull
    private Double lat;
    @NotNull
    private Double lng;

    public Double getLat() {
        return lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }
}
