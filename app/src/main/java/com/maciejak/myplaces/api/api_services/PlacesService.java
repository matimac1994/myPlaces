package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Mati on 01.12.2017.
 */

public interface PlacesService {

    @GET(ServerConfig.GET_ACTIVE_PLACES)
    Call<List<PlaceListResponse>> getAllActivePlaces();
}
