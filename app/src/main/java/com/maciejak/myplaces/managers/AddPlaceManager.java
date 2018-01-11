package com.maciejak.myplaces.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.PlacePhotoService;
import com.maciejak.myplaces.api.api_services.PlacesService;
import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.MapPhotoUtil;
import com.maciejak.myplaces.utils.TokenUtil;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    public void addPlace(LatLng latLng, String title, String description, String note, List<PlacePhotoResponse> placePhotos){
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

    public void deleteAddedPhotos(List<PlacePhotoResponse> photos){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                deleteAddedPhotosLocally(photos);
                break;
            case REMOTE:
                deleteAddedPhotosOnServer(photos);
                break;
            default:
                break;
        }
    }

    public void deleteAddedPhoto(PlacePhotoResponse photo, int position){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                deleteAddedPhotoLocally(photo, position);
                break;
            case REMOTE:
                deleteAddedPhotoOnServer(photo, position);
                break;
            default:
                break;
        }
    }

    private void deleteAddedPhotoOnServer(PlacePhotoResponse photo, int position) {
        Call<Void> call = mPlacePhotoService.deletePlacePhotoById(TokenUtil.getToken(), photo.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mAddPlaceResponseListener.onDeletePhoto(position);
                } else {
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void deleteAddedPhotoLocally(PlacePhotoResponse photo, int position) {
        File file = new File(photo.getPlacePhotoUrl());
        boolean isDeleted = file.delete();
        if (isDeleted){
            mAddPlaceResponseListener.onDeletePhoto(position);
        } else {
            mServerErrorResponseListener.onFailure(mContext.getString(R.string.problem_with_delete_photo));
        }
    }

    private void addPlaceLocally(LatLng latLng, String title, String description, String note, List<PlacePhotoResponse> placePhotos){
        String mapPhoto = new MapPhotoUtil(latLng.latitude, latLng.longitude).createUrlForMapImage();
        List<Uri> photos = new ArrayList<>();
        for (PlacePhotoResponse photoResponse : placePhotos){
            photos.add(Uri.parse(photoResponse.getPlacePhotoUrl()));
        }
        Place place = mPlaceRepository.savePlace(title, latLng, description, note, mapPhoto, photos);
        mAddPlaceResponseListener.onSuccessAddPlaceResponse();
    }

    private void addPlaceOnServer(LatLng latLng, String title, String description, String note, List<PlacePhotoResponse> placePhotos){
        AddPlaceRequest addPlaceRequest = createAddPlaceRequest(latLng, title, description, note, placePhotos);

        Call<AddPlaceResponse> call = mPlacesService.addPlace(TokenUtil.getToken(), addPlaceRequest);
        call.enqueue(new Callback<AddPlaceResponse>() {
            @Override
            public void onResponse(Call<AddPlaceResponse> call, Response<AddPlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mAddPlaceResponseListener.onSuccessAddPlaceResponse();
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

    private void uploadPhotoOnServer(Uri uri) {
        Call<PlacePhotoResponse> call = mPlacePhotoService.uploadPhoto(TokenUtil.getToken(), prepareFilePart("photo", uri));
        call.enqueue(new Callback<PlacePhotoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacePhotoResponse> call, @NonNull Response<PlacePhotoResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    removeFileFromDevice(uri);
                    mAddPlaceResponseListener.onUploadPhoto(response.body());
                } else {
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<PlacePhotoResponse> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void removeFileFromDevice(Uri uri) {
        File file = new File(uri.toString());
        file.delete();
    }

    private void savePhotoLocally(Uri uri) {
        PlacePhotoResponse placePhotoResponse = new PlacePhotoResponse();
        placePhotoResponse.setPlacePhotoUrl(uri.toString());
        mAddPlaceResponseListener.onUploadPhoto(placePhotoResponse);
    }

    private MultipartBody.Part prepareFilePart(String name, Uri uri) {
        File file = new File(uri.getPath());
        return MultipartBody.Part.createFormData(name, file.getName(), prepareFileRequestBody(uri));
    }

    private RequestBody prepareFileRequestBody(Uri uri){
        File file = new File(uri.getPath());

        RequestBody requestBody = RequestBody.create(
                MediaType.parse("multipart/form-data"),
                file
        );

        return requestBody;
    }

    private AddPlaceRequest createAddPlaceRequest(LatLng latLng, String title, String description, String note, List<PlacePhotoResponse> photos){
        AddPlaceRequest addPlaceRequest = new AddPlaceRequest();
        addPlaceRequest.setLatitude(latLng.latitude);
        addPlaceRequest.setLongitude(latLng.longitude);
        addPlaceRequest.setTitle(title);
        addPlaceRequest.setDescription(description);
        addPlaceRequest.setNote(note);
        addPlaceRequest.setPlacePhotosIds(getPlacePhotosIds(photos));
        return addPlaceRequest;
    }

    private void deleteAddedPhotosOnServer(List<PlacePhotoResponse> photos) {
        IdsRequest idsRequest = new IdsRequest();
        List<Long> ids = getPlacePhotosIds(photos);
        idsRequest.setIds(ids);

        Call<Void> call = mPlacePhotoService.deletePlacePhotosByIds(TokenUtil.getToken(), idsRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void deleteAddedPhotosLocally(List<PlacePhotoResponse> photos) {
        for(int i=0; i < photos.size(); i++){
            File file = new File(photos.get(i).getPlacePhotoUrl());
            file.delete();
        }
    }

    private List<Long> getPlacePhotosIds(List<PlacePhotoResponse> photos){
        List<Long> ids = new ArrayList<>();
        for (PlacePhotoResponse placePhotoResponse : photos){
            ids.add(placePhotoResponse.getId());
        }
        return ids;
    }


    public interface AddPlaceResponseListener{
        void onSuccessAddPlaceResponse();
        void onUploadPhoto(PlacePhotoResponse placePhotoResponse);
        void onDeletePhoto(int position);
    }
}
