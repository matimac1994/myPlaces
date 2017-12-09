package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 05.12.2017.
 */

public class PlacePhotoResponse {

    private Long id;
    private String photoUrl;
    private Long placeId;

    public PlacePhotoResponse() {
    }

    public PlacePhotoResponse(Long id, String photoUrl, Long placeId) {
        this.id = id;
        this.photoUrl = photoUrl;
        this.placeId = placeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}
