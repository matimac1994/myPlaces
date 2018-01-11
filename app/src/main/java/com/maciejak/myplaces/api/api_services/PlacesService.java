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

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Mati on 01.12.2017.
 */

public interface PlacesService {

    @GET(ServerConfig.BASE_URL)
    Call<List<PlaceResponse>> getAllPlaces();

    @GET(ServerConfig.BASE_URL + "/active")
    Call<List<PlaceListResponse>> getAllActivePlaces(@Header(ServerConfig.TOKEN_NAME) String token);

    @GET(ServerConfig.BASE_URL + "/archived")
    Call<List<PlaceListResponse>> getAllArchivePlaces(@Header(ServerConfig.TOKEN_NAME) String token);

    @GET(ServerConfig.BASE_URL + "/map")
    Call<List<PlaceMapResponse>> getActivePlacesOnMap(@Header(ServerConfig.TOKEN_NAME) String token);

    @PATCH(ServerConfig.BASE_URL + "/restore")
    Call<Void> restorePlaces(@Header(ServerConfig.TOKEN_NAME) String token, @Body IdsRequest idsRequest);

    @POST(ServerConfig.BASE_URL + "/delete")
    Call<Void> deletePlaces(@Header(ServerConfig.TOKEN_NAME) String token, @Body IdsRequest idsRequest);

    @POST(ServerConfig.BASE_URL + "/add")
    Call<AddPlaceResponse> addPlace(@Header(ServerConfig.TOKEN_NAME) String token,
                                    @Body AddPlaceRequest addPlaceRequest);

    @POST(ServerConfig.BASE_URL + "/edit")
    Call<Void> editPlace(@Header(ServerConfig.TOKEN_NAME) String token, @Body EditPlaceRequest editPlaceRequest);

    @GET(ServerConfig.BASE_URL + "/{placeId}")
    Call<PlaceResponse> getPlaceById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("placeId") Long placeId);

    @DELETE(ServerConfig.BASE_URL + "/delete/{placeId}")
    Call<Void> deletePlaceById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("placeId") Long placeId);

    @PATCH(ServerConfig.BASE_URL + "/restore/{placeId}")
    Call<Void> restorePlaceById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("placeId") Long placeId);

    @PATCH(ServerConfig.BASE_URL + "/archive/{placeId}")
    Call<Void> archivePlaceById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("placeId") Long placeId);
}