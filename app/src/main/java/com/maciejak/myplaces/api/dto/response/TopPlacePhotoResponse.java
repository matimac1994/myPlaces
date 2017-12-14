package com.maciejak.myplaces.api.dto.response;

public class TopPlacePhotoResponse {

    private Long id;
    private String photo;
    private Long placeId;

    public TopPlacePhotoResponse() {
    }

    public TopPlacePhotoResponse(Long id, String photo, Long placeId) {
        this.id = id;
        this.photo = photo;
        this.placeId = placeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}
