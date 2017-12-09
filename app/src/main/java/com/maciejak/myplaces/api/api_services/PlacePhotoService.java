package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by Mati on 05.12.2017.
 */

public interface PlacePhotoService {

    @GET(ServerConfig.PLACE_PHOTOS)
    Call<List<PlacePhotoResponse>> getPlacePhotos();

    @GET(ServerConfig.PLACE_PHOTOS + "/byids")
    Call<List<PlacePhotoResponse>> getPlacePhotosByIds(@Body IdsRequest idsRequest);

    @GET(ServerConfig.PLACE_PHOTOS + "/{photoId}")
    Call<PlacePhotoResponse> getPlacePhotoById(@Path("photoId") Long photoId);

    @POST(ServerConfig.PLACE_PHOTOS + "/delete")
    Call<Void> deletePlacePhotosByIds(@Body IdsRequest idsRequest);

    @DELETE(ServerConfig.PLACE_PHOTOS + "/delete/{photoId}")
    Call<Void> deletePlacePhotoById(@Path("photoId") Long photoId);

}
