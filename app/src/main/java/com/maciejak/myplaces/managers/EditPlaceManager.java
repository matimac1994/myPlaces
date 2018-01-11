package com.maciejak.myplaces.managers;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.PlacePhotoService;
import com.maciejak.myplaces.api.api_services.PlacesService;
import com.maciejak.myplaces.api.dto.request.EditPlaceRequest;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.api.mappers.PlacePhotoMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.repositories.PlacePhotoRepository;
import com.maciejak.myplaces.repositories.PlaceRepository;
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

import static com.maciejak.myplaces.utils.FileUtils.removeFileFromDevice;

/**
 * Created by Mati on 03.12.2017.
 */

public class EditPlaceManager extends BaseRemoteManager {

    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlacePhotoMapper mPlacePhotoMapper = PlacePhotoMapper.INSTANCE;
    private PlaceRepository mPlaceRepository = new PlaceRepository();
    private PlacePhotoRepository mPlacePhotoRepository = new PlacePhotoRepository();
    private EditPlaceResponseListener mEditPlaceResponseListener;
    private ServerErrorResponseListener mServerErrorResponseListener;
    private PlacesService mPlacesService;
    private PlacePhotoService mPlacePhotoService;

    public EditPlaceManager(Context context, ServerErrorResponseListener serverErrorResponseListener,
                            EditPlaceResponseListener editPlaceResponseListener) {
        super(context);
        this.mEditPlaceResponseListener = editPlaceResponseListener;
        mPlacesService = mRetrofit.create(PlacesService.class);
        this.mPlacePhotoService = mRetrofit.create(PlacePhotoService.class);
        this.mServerErrorResponseListener = serverErrorResponseListener;
    }

    public void getPlaceById(Long id){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                getPlaceByIdLocally(id);
                break;
            case REMOTE:
                getPlaceByIdFromServer(id);
                break;
            default:
                break;
        }
    }

    private void getPlaceByIdLocally(Long id){
        Place place =  mPlaceRepository.getPlaceById(id);
        if (place != null){
            PlaceResponse placeResponse = convertPlaceToPlaceResponse(place);
            mEditPlaceResponseListener.onGetPlace(placeResponse);
        } else {
            mEditPlaceResponseListener.onGetPlaceError(mContext.getString(R.string.problem_with_show_place));
        }
    }

    private void getPlaceByIdFromServer(Long id){
        Call<PlaceResponse> call = mPlacesService.getPlaceById(TokenUtil.getToken(), id);
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mEditPlaceResponseListener.onGetPlace(response.body());
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mEditPlaceResponseListener.onGetPlaceError("Problem z wyświetleniem miejsca");
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    public void editPlace(Long id,
                          String title,
                          String description,
                          String note,
                          List<PlacePhotoResponse> placePhotos){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                editPlaceLocally(id, title, description, note, placePhotos);
                break;
            case REMOTE:
                editPlaceOnServer(id, title, description, note, placePhotos);
                break;
            default:
                break;
        }
    }

    private void editPlaceLocally(Long id,
                                  String title,
                                  String description,
                                  String note,
                                  List<PlacePhotoResponse> placePhotos){

        Place place = mPlaceRepository.getPlaceById(id);
        List<PlacePhoto> photos = place.getPhotos();
        List<PlacePhoto> editedPhotos = mPlacePhotoRepository.getPlacePhotoByIds(getIdsFromPlacePhotoResponseList(placePhotos));

        for (PlacePhoto placePhoto : editedPhotos){
            placePhoto.setPlace(place);
        }

        for (PlacePhoto placePhoto : photos){
            if (!editedPhotos.contains(placePhoto)){
                removeFileFromDevice(placePhoto.getPlacePhotoUrl());
                placePhoto.delete();
            }
        }

        mPlaceRepository.editPlace(id,
                title,
                description,
                note,
                editedPhotos);
        mEditPlaceResponseListener.onSuccessResponse();
    }

    private void editPlaceOnServer(Long id,
                                   String title,
                                   String description,
                                   String note,
                                   List<PlacePhotoResponse> placePhotos){
        EditPlaceRequest editPlaceRequest = createEditPlaceRequest(id, title, description, note, placePhotos);

        Call<Void> call = mPlacesService.editPlace(TokenUtil.getToken(), editPlaceRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mEditPlaceResponseListener.onSuccessResponse();
                } else{
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });

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

    private void uploadPhotoOnServer(Uri uri) {
        Call<PlacePhotoResponse> call = mPlacePhotoService.uploadPhoto(TokenUtil.getToken(), prepareFilePart("photo", uri));
        call.enqueue(new Callback<PlacePhotoResponse>() {
            @Override
            public void onResponse(@NonNull Call<PlacePhotoResponse> call, @NonNull Response<PlacePhotoResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    removeFileFromDevice(uri);
                    mEditPlaceResponseListener.onUploadPhoto(response.body());
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

    private void savePhotoLocally(Uri uri) {
        PlacePhoto placePhoto = mPlacePhotoRepository.createPlacePhoto(uri.toString(), null);
        mEditPlaceResponseListener.onUploadPhoto(mPlacePhotoMapper.placePhotoToPlacePhotoResponse(placePhoto));
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

    private EditPlaceRequest createEditPlaceRequest(Long id, String title, String description,
                                                    String note, List<PlacePhotoResponse> photos){
        EditPlaceRequest editPlaceRequest = new EditPlaceRequest();
        editPlaceRequest.setId(id);
        editPlaceRequest.setTitle(title);
        editPlaceRequest.setDescription(description);
        editPlaceRequest.setNote(note);
        editPlaceRequest.setPhotos(photos);
        return editPlaceRequest;
    }

    private List<PlacePhoto> convertListPlacePhotoResponseToListPlacePhoto(List<PlacePhotoResponse> placePhotos){
        List<PlacePhoto> photos = new ArrayList<>();
        for (PlacePhotoResponse placePhotoResponse : placePhotos){
            photos.add(convertPlacePhotoResponseToPlacePhoto(placePhotoResponse));
        }
        return photos;
    }

    private PlacePhoto convertPlacePhotoResponseToPlacePhoto(PlacePhotoResponse placePhotoResponse){
        PlacePhoto placePhoto = mPlacePhotoMapper.placePhotoResponseToPlacePhoto(placePhotoResponse);
        Place place = mPlaceRepository.getPlaceById(placePhotoResponse.getPlaceId());
        if (place != null){
            placePhoto.setPlace(place);
        }
        return placePhoto;
    }

    private List<Long> getIdsFromPlacePhotoResponseList(List<PlacePhotoResponse> photos){
        List<Long> ids = new ArrayList<>();
        for (PlacePhotoResponse placePhotoResponse : photos){
            ids.add(placePhotoResponse.getId());
        }
        return ids;
    }

    private PlaceResponse convertPlaceToPlaceResponse(Place place){
        return mPlaceMapper.placeToPlaceResponse(place);
    }

    public interface EditPlaceResponseListener{
        void onGetPlace(PlaceResponse placeResponse);
        void onSuccessResponse();
        void onGetPlaceError(String string);
        void onUploadPhoto(PlacePhotoResponse placePhotoResponse);
    }
}
