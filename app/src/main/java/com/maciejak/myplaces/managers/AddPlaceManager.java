package com.maciejak.myplaces.managers;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.PlacePhotoService;
import com.maciejak.myplaces.api.api_services.PlacesService;
import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.FileUtils;
import com.maciejak.myplaces.utils.MapPhotoUtil;
import com.maciejak.myplaces.utils.TokenUtil;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Mati on 30.11.2017.
 */

public class AddPlaceManager extends BaseRemoteManager {

    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlaceRepository mPlaceRepository = new PlaceRepository();
    private AddPlaceResponseListener mAddPlaceResponseListener;
    private ServerErrorResponseListener mServerErrorResponseListener;
    private PlacesService mPlacesService;
    private PlacePhotoService mPlacePhotoService;

    public AddPlaceManager(Context context, AddPlaceResponseListener addPlaceResponseListener, ServerErrorResponseListener serverErrorResponseListener) {
        super(context);
        this.mAddPlaceResponseListener = addPlaceResponseListener;
        this.mServerErrorResponseListener = serverErrorResponseListener;
        mPlacesService = mRetrofit.create(PlacesService.class);
        mPlacePhotoService = mRetrofit.create(PlacePhotoService.class);
    }

    public void addPlace(LatLng latLng, String title, String description, String note, List<Uri> placePhotos){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                addPlaceLocally(latLng, title, description, note, placePhotos);
                break;
            case REMOTE:
                addPlaceOnServer(latLng, title, description, note, placePhotos);
                break;
            default:
                break;
        }
    }

    public void addPhoto(Uri uri){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                savePhotoLocally(uri);
                break;
            case REMOTE:
                uploadPhotoOnServer(uri);
                break;
            default:
                break;
        }
    }

    private void addPlaceLocally(LatLng latLng, String title, String description, String note, List<Uri> placePhotos){
        String mapPhoto = new MapPhotoUtil(latLng.latitude, latLng.longitude).createUrlForMapImage();
        Place place = mPlaceRepository.savePlace(title, latLng, description, note, mapPhoto, placePhotos);
        mAddPlaceResponseListener.onSuccessResponse();
    }

    private void addPlaceOnServer(LatLng latLng, String title, String description, String note, List<Uri> placePhotos){
        AddPlaceRequest addPlaceRequest = createAddPlaceRequest(latLng, title, description, note);
        sendRequest(addPlaceRequest, placePhotos);
    }

    private void uploadPhotoOnServer(Uri uri) {
        Call<PlacePhotoResponse> call = mPlacePhotoService.uploadPhoto(TokenUtil.getToken(), prepareFilePart("photo", uri));
        call.enqueue(new Callback<PlacePhotoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacePhotoResponse> call, @NonNull Response<PlacePhotoResponse> response) {
                System.out.println(response.body().getPhotoUrl());
            }

            @Override
            public void onFailure(Call<PlacePhotoResponse> call, Throwable t) {

            }
        });
    }

    private void savePhotoLocally(Uri uri) {
        PlacePhotoResponse placePhotoResponse = new PlacePhotoResponse();
        placePhotoResponse.setPhotoUrl(uri.toString());
        mAddPlaceResponseListener.onUploadPhoto(placePhotoResponse);
    }

    private void sendRequest(AddPlaceRequest addPlaceRequest, List<Uri> photos){

//        List<MultipartBody.Part> uploadPhotos = createPhotosMultipartBody(photos);

        Call<AddPlaceResponse> call = mPlacesService.addPlace(TokenUtil.getToken(), addPlaceRequest);
//        Call<AddPlaceResponse> call = mPlacesService.addPlace(TokenUtil.getToken(),
//                createPartFromAddPlaceRequest(addPlaceRequest),
//                uploadPhotos);

        call.enqueue(new Callback<AddPlaceResponse>() {
            @Override
            public void onResponse(Call<AddPlaceResponse> call, Response<AddPlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
//                    mPlacePhotoService.uploadPhoto(TokenUtil.getToken(), response.body().getId(), prepareFilePart("abc", photos.get(0)));
                    mAddPlaceResponseListener.onSuccessResponse();
                } else{
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<AddPlaceResponse> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

//    private List<MultipartBody.Part> createPhotosMultipartBody(List<Uri> photos) {
//        List<MultipartBody.Part> uploadPhotos = new ArrayList<>();
//        int i = 0;
//        for (Uri uri : photos){
//            uploadPhotos.add(prepareFilePart("photo" + i++, uri));
//        }
//
//        return uploadPhotos;
//    }

    private MultipartBody.Part prepareFilePart(String name, Uri uri) {
        File file = new File(uri.getPath());
        return MultipartBody.Part.createFormData(name, file.getName(), prepareFileRequestBody(uri));
    }

    private RequestBody createPartFromAddPlaceRequest(AddPlaceRequest addPlaceRequest){

        String json = null;
        try {
            json = mapper.writeValueAsString(addPlaceRequest);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return RequestBody.create(MediaType.parse("multipart/form-data"), json);
    }

    private RequestBody prepareFileRequestBody(Uri uri){
        File file = new File(uri.getPath());

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                file
        );

        return requestBody;
    }

    private AddPlaceRequest createAddPlaceRequest(LatLng latLng, String title, String description, String note){
        AddPlaceRequest addPlaceRequest = new AddPlaceRequest();
        addPlaceRequest.setLatitude(latLng.latitude);
        addPlaceRequest.setLongitude(latLng.longitude);
        addPlaceRequest.setTitle(title);
        addPlaceRequest.setDescription(description);
        addPlaceRequest.setNote(note);
        return addPlaceRequest;
    }

    public void trimCache(List<Uri> photos) {
        for(int i=0; i < photos.size(); i++){
            File file = new File(photos.get(i).toString());
            file.delete();
        }
    }

    public interface AddPlaceResponseListener{
        void onSuccessResponse();
        void onUploadPhoto(PlacePhotoResponse placePhotoResponse);
    }
}
