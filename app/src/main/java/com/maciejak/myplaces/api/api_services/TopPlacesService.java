package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponse;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponseList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

/**
 * Created by Mati on 14.12.2017.
 */

public interface TopPlacesService {

    @GET(ServerConfig.BASE_URL + "/top")
    Call<List<TopPlaceResponseList>> getTopPlaces(@Header(ServerConfig.TOKEN_NAME) String token);

    @GET(ServerConfig.BASE_URL + "/top/{topPlaceId}")
    Call<TopPlaceResponse> getTopPlaceById(@Header(ServerConfig.TOKEN_NAME) String token,
            @Path("topPlaceId") Long topPlaceId);
}
