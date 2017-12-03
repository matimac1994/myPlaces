package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.response.PlaceMapResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mati on 03.12.2017.
 */

public interface PlaceMapService {

    @GET(ServerConfig.GET_MAP_PLACES)
    Call<List<PlaceMapResponse>> getPlaces();
}
