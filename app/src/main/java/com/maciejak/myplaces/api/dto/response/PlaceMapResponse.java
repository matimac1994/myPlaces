package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 02.12.2017.
 */

public class PlaceMapResponse {

    private Long id;
    private Double latitude;
    private Double longitude;
    private String title;
    private String description;

    public PlaceMapResponse() {
    }

    public PlaceMapResponse(Long id, Double latitude, Double longitude, String title, String description) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}