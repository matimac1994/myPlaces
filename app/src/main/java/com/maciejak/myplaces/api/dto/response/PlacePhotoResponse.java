package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 05.12.2017.
 */

public class PlacePhotoResponse {

    private Long id;
    private String placePhotoUrl;
    private Long placeId;

    public PlacePhotoResponse() {
    }

    public PlacePhotoResponse(Long id, String placePhotoUrl, Long placeId) {
        this.id = id;
        this.placePhotoUrl = placePhotoUrl;
        this.placeId = placeId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPlacePhotoUrl() {
        return placePhotoUrl;
    }

    public void setPlacePhotoUrl(String placePhotoUrl) {
        this.placePhotoUrl = placePhotoUrl;
    }

    public Long getPlaceId() {
        return placeId;
    }

    public void setPlaceId(Long placeId) {
        this.placeId = placeId;
    }
}
