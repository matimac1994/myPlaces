package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.Path;

/**
 * Created by Mati on 03.12.2017.
 */

public interface ShowPlaceService {

    @GET(ServerConfig.GET_PLACE_BY_ID)
    Call<PlaceResponse> getPlaceById(@Path("placeId") Long placeId);

    @DELETE(ServerConfig.DELETE_PLACE_BY_ID)
    Call<Void> deletePlaceById(@Path("placeId") Long placeId);

    @PATCH(ServerConfig.RESTORE_PLACE_BY_ID)
    Call<Void> restorePlaceById(@Path("placeId") Long placeId);

    @PATCH(ServerConfig.ARCHIVE_PLACE_BY_ID)
    Call<Void> archivePlaceById(@Path("placeId") Long placeId);
}
