package com.maciejak.myplaces.api.dto.response;

import com.maciejak.myplaces.model.PlacePhoto;

import java.util.List;

/**
 * Created by Mati on 02.12.2017.
 */

public class PlaceResponse {

    private Long id;
    private Double latitude;
    private Double longitude;
    private String title;
    private String description;
    private String note;
    private String mapPhoto;
    private String deletedAt;
    private List<PlacePhotoResponse> photos;

    public PlaceResponse() {
    }

    public PlaceResponse(Long id, Double latitude, Double longitude, String title, String description,
                         String note, String mapPhoto, String deletedAt, List<PlacePhotoResponse> photos) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.note = note;
        this.mapPhoto = mapPhoto;
        this.deletedAt = deletedAt;
        this.photos = photos;
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

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMapPhoto() {
        return mapPhoto;
    }

    public void setMapPhoto(String mapPhoto) {
        this.mapPhoto = mapPhoto;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public List<PlacePhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PlacePhotoResponse> photos) {
        this.photos = photos;
    }
}

