package com.maciejak.myplaces.api.dto.response;

/**
 * Created by Mati on 30.11.2017.
 */

public class AddPlaceResponse extends BaseResponse{

    private Long id;

    public AddPlaceResponse() {
    }

    public AddPlaceResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
