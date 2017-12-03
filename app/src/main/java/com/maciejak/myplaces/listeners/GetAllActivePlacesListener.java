package com.maciejak.myplaces.listeners;

import com.maciejak.myplaces.api.dto.response.PlaceListResponse;

import java.util.List;

/**
 * Created by Mati on 01.12.2017.
 */

public interface GetAllActivePlacesListener {
    void onGetAllActivePlaces(List<PlaceListResponse> places);
}
