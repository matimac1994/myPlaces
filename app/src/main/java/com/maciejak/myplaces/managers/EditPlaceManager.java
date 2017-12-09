package com.maciejak.myplaces.managers;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.EditPlaceService;
import com.maciejak.myplaces.api.api_services.PlacePhotoService;
import com.maciejak.myplaces.api.dto.request.EditPlaceRequest;
import com.maciejak.myplaces.api.dto.request.IdsRequest;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.api.mappers.PlacePhotoMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 03.12.2017.
 */

public class EditPlaceManager extends BaseRemoteManager {

    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlacePhotoMapper mPlacePhotoMapper = PlacePhotoMapper.INSTANCE;
    private PlaceRepository mPlaceRepository = new PlaceRepository();
    private EditPlaceResponseListener mEditPlaceResponseListener;

    private EditPlaceService mEditPlaceService;
    private PlacePhotoService mPlacePhotoService;

    public EditPlaceManager(Context context, EditPlaceResponseListener editPlaceResponseListener) {
        super(context);
        this.mEditPlaceResponseListener = editPlaceResponseListener;
        this.mEditPlaceService = mRetrofit.create(EditPlaceService.class);
        this.mPlacePhotoService = mRetrofit.create(PlacePhotoService.class);
    }

    public void editPlace(Long id,
                          String title,
                          String description,
                          String note,
                          List<PlacePhotoResponse> placePhotos,
                          List<PlacePhotoResponse> photosToDelete){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                editPlaceLocally(id, title, description, note, placePhotos, photosToDelete);
                break;
            case REMOTE:
                editPlaceOnServer(id, title, description, note, placePhotos, photosToDelete);
                break;
            default:
                break;
        }
    }

    private void editPlaceLocally(Long id,
                                  String title,
                                  String description,
                                  String note,
                                  List<PlacePhotoResponse> placePhotos,
                                  List<PlacePhotoResponse> photosToDelete){
        mPlaceRepository.editPlace(id,
                title,
                description,
                note,
                convertListPlacePhotoResponseToListPlacePhoto(placePhotos),
                convertListPlacePhotoResponseToListPlacePhoto(photosToDelete));
    }

    private void editPlaceOnServer(Long id,
                                   String title,
                                   String description,
                                   String note,
                                   List<PlacePhotoResponse> placePhotos,
                                   List<PlacePhotoResponse> photosToDelete){
        EditPlaceRequest editPlaceRequest = createEditPlaceRequest(id, title, description, note);
        sendEditPlaceRequest(editPlaceRequest);

        editPlacePhotosOnServer(placePhotos, photosToDelete);
    }

    private void editPlaceOnServer(Place place){
        EditPlaceRequest editPlaceRequest = mPlaceMapper.placeToEditPlaceRequest(place);
        sendEditPlaceRequest(editPlaceRequest);
    }

    private void editPlacePhotosOnServer(List<PlacePhotoResponse> placePhotos, List<PlacePhotoResponse> photosToDelete) {

        IdsRequest placePhotosIdsToDelete = new IdsRequest();
        placePhotosIdsToDelete.setIds(getIdsFromPlacePhotoResponseList(photosToDelete));
        sendDeletePlacePhotosRequest(placePhotosIdsToDelete);

    }

    private void sendEditPlaceRequest(EditPlaceRequest editPlaceRequest){
        ServerErrorResponseListener listener = ((ServerErrorResponseListener)mContext);

        Call<Void> call = mEditPlaceService.editPlace(editPlaceRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mEditPlaceResponseListener.onSuccessResponse();
                } else{
                    listener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                listener.onFailure(mContext.getString(R.string.server_error));
            }
        });

    }

    private void sendDeletePlacePhotosRequest(IdsRequest placePhotosIdsToDelete) {
        Call<Void> call = mPlacePhotoService.deletePlacePhotosByIds(placePhotosIdsToDelete);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    private EditPlaceRequest createEditPlaceRequest(Long id, String title, String description, String note){
        EditPlaceRequest editPlaceRequest = new EditPlaceRequest();
        editPlaceRequest.setId(id);
        editPlaceRequest.setTitle(title);
        editPlaceRequest.setDescription(description);
        editPlaceRequest.setNote(note);
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

    public interface EditPlaceResponseListener{
        void onSuccessResponse();
    }
}
