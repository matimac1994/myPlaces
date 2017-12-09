package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.request.EditPlaceRequest;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.api.dto.response.PlaceMapResponse;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Mati on 01.12.2017.
 */

public interface PlacesService {

    @GET(ServerConfig.BASE_URL)
    Call<List<PlaceResponse>> getAllPlaces();

    @GET(ServerConfig.BASE_URL + "/active")
    Call<List<PlaceListResponse>> getAllActivePlaces();

    @GET(ServerConfig.BASE_URL + "/archived")
    Call<List<PlaceListResponse>> getAllArchivePlaces();

    @GET(ServerConfig.BASE_URL + "/map")
    Call<List<PlaceMapResponse>> getActivePlacesOnMap();

    @PATCH(ServerConfig.BASE_URL + "/restore")
    Call<Void> restorePlaces(@Body IdsRequest idsRequest);

    @POST(ServerConfig.BASE_URL + "/delete")
    Call<Void> deletePlaces(@Body IdsRequest idsRequest);

    @POST(ServerConfig.BASE_URL + "/add")
    Call<AddPlaceResponse> addPlace(@Body AddPlaceRequest addPlaceRequest);

    @POST(ServerConfig.BASE_URL + "/edit")
    Call<Void> editPlace(@Body EditPlaceRequest editPlaceRequest);

    @GET(ServerConfig.BASE_URL + "/{placeId}")
    Call<PlaceResponse> getPlaceById(@Path("placeId") Long placeId);

    @DELETE(ServerConfig.BASE_URL + "/delete/{placeId}")
    Call<Void> deletePlaceById(@Path("placeId") Long placeId);

    @PATCH(ServerConfig.BASE_URL + "/restore/{placeId}")
    Call<Void> restorePlaceById(@Path("placeId") Long placeId);

    @PATCH(ServerConfig.BASE_URL + "/archive/{placeId}")
    Call<Void> archivePlaceById(@Path("placeId") Long placeId);
}