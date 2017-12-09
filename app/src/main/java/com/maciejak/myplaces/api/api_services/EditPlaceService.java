package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.EditPlaceRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mati on 05.12.2017.
 */

public interface EditPlaceService {

    @POST(ServerConfig.EDIT_PLACE)
    Call<Void> editPlace(@Body EditPlaceRequest editPlaceRequest);
}
