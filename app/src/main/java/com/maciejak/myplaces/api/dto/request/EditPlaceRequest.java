package com.maciejak.myplaces.api.dto.request;

import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;

import java.util.List;

/**
 * Created by Mati on 03.12.2017.
 */

public class EditPlaceRequest {

    private Long id;
    private String title;
    private String description;
    private String note;
    private List<PlacePhotoResponse> photos;

    public EditPlaceRequest() {
    }

    public EditPlaceRequest(Long id, String title, String description,
                            String note, List<PlacePhotoResponse> photos) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.note = note;
        this.photos = photos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<PlacePhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PlacePhotoResponse> photos) {
        this.photos = photos;
    }
}