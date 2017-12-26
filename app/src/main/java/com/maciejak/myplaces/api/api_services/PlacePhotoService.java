package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by Mati on 05.12.2017.
 */

public interface PlacePhotoService {

    @GET(ServerConfig.PLACE_PHOTOS)
    Call<List<PlacePhotoResponse>> getPlacePhotos(@Header(ServerConfig.TOKEN_NAME) String token);

    @GET(ServerConfig.PLACE_PHOTOS + "/byids")
    Call<List<PlacePhotoResponse>> getPlacePhotosByIds(@Header(ServerConfig.TOKEN_NAME) String token, @Body IdsRequest idsRequest);

    @GET(ServerConfig.PLACE_PHOTOS + "/{photoId}")
    Call<PlacePhotoResponse> getPlacePhotoById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("photoId") Long photoId);

    @Multipart
    @POST(ServerConfig.PLACE_PHOTOS + "/upload")
    Call<PlacePhotoResponse> uploadPhoto(@Header(ServerConfig.TOKEN_NAME) String token,
                            @Part MultipartBody.Part photo);

    @POST(ServerConfig.PLACE_PHOTOS + "/upload/{placeId}")
    Call<Void> uploadPhoto(@Header(ServerConfig.TOKEN_NAME) String token,
                           @Path("placeId") Long placeId,
                           @Part("photo") MultipartBody.Part photo);

    @POST(ServerConfig.PLACE_PHOTOS + "/upload/{placeId}")
    Call<Void> uploadPhotos(@Header(ServerConfig.TOKEN_NAME) String token,
                           @Path("placeId") Long placeId,
                           @Part List<MultipartBody.Part> photos);


    @POST(ServerConfig.PLACE_PHOTOS + "/delete")
    Call<Void> deletePlacePhotosByIds(@Header(ServerConfig.TOKEN_NAME) String token, @Body IdsRequest idsRequest);

    @DELETE(ServerConfig.PLACE_PHOTOS + "/delete/{photoId}")
    Call<Void> deletePlacePhotoById(@Header(ServerConfig.TOKEN_NAME) String token, @Path("photoId") Long photoId);

}
