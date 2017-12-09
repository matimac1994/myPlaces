package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;

/**
 * Created by Mati on 03.12.2017.
 */

public interface ArchivePlacesService {

    @GET(ServerConfig.ARCHIVE_PLACES_SPACE)
    Call<List<PlaceListResponse>> getArchivePlaces();

    @POST(ServerConfig.ARCHIVE_PLACES_SPACE + "/delete")
    Call<Void> deletePlaces(@Body IdsRequest idsRequest);

    @PATCH(ServerConfig.ARCHIVE_PLACES_SPACE + "/restore")
    Call<Void> restorePlaces(@Body IdsRequest idsRequest);
}
