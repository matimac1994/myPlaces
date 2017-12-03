package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 01.12.2017.
 */

public class PlaceListResponse extends BaseResponse {

    private Long id;
    private String title;
    private String description;
    private String mapPhoto;

    public PlaceListResponse() {
    }

    public PlaceListResponse(Long id, String title, String description, String mapPhoto) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.mapPhoto = mapPhoto;
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

    public String getMapPhoto() {
        return mapPhoto;
    }

    public void setMapPhoto(String mapPhoto) {
        this.mapPhoto = mapPhoto;
    }
}
