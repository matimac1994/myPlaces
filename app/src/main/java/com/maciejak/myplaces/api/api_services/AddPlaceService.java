package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mati on 30.11.2017.
 */

public interface AddPlaceService {

    @POST(ServerConfig.ADD_PLACE)
    Call<AddPlaceResponse> addPlace(@Body AddPlaceRequest addPlaceRequest);
}
