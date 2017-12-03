package com.maciejak.myplaces.api.dto.request;

import java.util.List;
/**
 * Created by Mati on 01.12.2017.
 */

public class PlaceIdsRequest {

    List<Long> placeIds;

    public PlaceIdsRequest() {
    }

    public PlaceIdsRequest(List<Long> placeIds) {
        this.placeIds = placeIds;
    }

    public List<Long> getPlaceIds() {
        return placeIds;
    }

    public void setPlaceIds(List<Long> placeIds) {
        this.placeIds = placeIds;
    }
}
