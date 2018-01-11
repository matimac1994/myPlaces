package com.maciejak.myplaces.api.dto.request;

import java.util.List;

public class AddPlaceRequest {

    private Double latitude;
    private Double longitude;
    private String title;
    private String description;
    private String note;
    private List<Long> placePhotosIds;

    public AddPlaceRequest() {
    }

    public AddPlaceRequest(Double latitude, Double longitude, String title, String description, String note, List<Long> placePhotosIds) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.note = note;
        this.placePhotosIds = placePhotosIds;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<Long> getPlacePhotosIds() {
        return placePhotosIds;
    }

    public void setPlacePhotosIds(List<Long> placePhotosIds) {
        this.placePhotosIds = placePhotosIds;
    }
}
