package com.maciejak.myplaces.managers;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.AddPlaceService;
import com.maciejak.myplaces.api.dto.request.AddPlaceRequest;
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.MapPhotoUtil;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.util.List;

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

    public AddPlaceManager(Context context, AddPlaceResponseListener addPlaceResponseListener) {
        super(context);
        this.mAddPlaceResponseListener = addPlaceResponseListener;
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

    private void addPlaceLocally(LatLng latLng, String title, String description, String note, List<Uri> placePhotos){
        String mapPhoto = new MapPhotoUtil(latLng.latitude, latLng.longitude).createUrlForMapImage();
        Place place = mPlaceRepository.savePlace(title, latLng, description, note, mapPhoto, placePhotos);
    }

    private void addPlaceOnServer(LatLng latLng, String title, String description, String note, List<Uri> placePhotos){
        AddPlaceRequest addPlaceRequest = createAddPlaceRequest(latLng, title, description, note);
        sendRequest(addPlaceRequest);
    }

    private void addPlaceOnServer(Place place){
        AddPlaceRequest addPlaceRequest = mPlaceMapper.placeToAddPlaceRequest(place);
        sendRequest(addPlaceRequest);
    }

    private void sendRequest(AddPlaceRequest addPlaceRequest){
        AddPlaceService addPlaceService = mRetrofit.create(AddPlaceService.class);

        ServerErrorResponseListener listener = ((ServerErrorResponseListener)mContext);

        Call<AddPlaceResponse> call = addPlaceService.addPlace(addPlaceRequest);
        call.enqueue(new Callback<AddPlaceResponse>() {
            @Override
            public void onResponse(Call<AddPlaceResponse> call, Response<AddPlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mAddPlaceResponseListener.onSuccessResponse(response.body());
                } else{
                    listener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<AddPlaceResponse> call, Throwable t) {
                listener.onFailure(mContext.getString(R.string.server_error));
            }
        });
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

    public interface AddPlaceResponseListener{
        void onSuccessResponse(AddPlaceResponse addPlaceResponse);
    }
}
