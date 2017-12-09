package com.maciejak.myplaces.managers;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.PlacesService;
import com.maciejak.myplaces.api.dto.response.PlaceListResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 01.12.2017.
 */

public class PlaceListManager extends BaseRemoteManager {

    private GetAllActivePlacesListener getPlacesListener;
    private ServerErrorResponseListener mServerErrorResponseListener;
    private PlaceRepository mPlaceRepository = new PlaceRepository();
    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlacesService mPlacesService;

    public PlaceListManager(Context context, ServerErrorResponseListener serverErrorResponseListener, GetAllActivePlacesListener getPlacesListener) {
        super(context);
        this.getPlacesListener = getPlacesListener;
        this.mServerErrorResponseListener = serverErrorResponseListener;
        this.mPlacesService = mRetrofit.create(PlacesService.class);
    }

    public void getPlaces(){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                getPlacesLocally();
                break;
            case REMOTE:
                getPlacesFromServer();
                break;
            default:
                break;
        }
    }

    public void restorePlaceById(Long id, int position){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                restorePlaceByIdLocally(id, position);
                break;
            case REMOTE:
                restorePlaceByIdOnServer(id, position);
                break;
            default:
                break;
        }
    }

    public void archivePlace(PlaceListResponse place, int position){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                archivePlaceLocally(place, position);
                break;
            case REMOTE:
                archivePlaceOnServer(place, position);
                break;
            default:
                break;
        }
    }

    private void getPlacesLocally(){
        List<Place> places = mPlaceRepository.getAllVisiblePlaces();
        List<PlaceListResponse> placesResponse = new ArrayList<>();
        for (Place place : places){
            placesResponse.add(convertToListPlaceListResponse(place));
        }
        getPlacesListener.onGetAllActivePlaces(placesResponse);
    }

    private void getPlacesFromServer(){
        Call<List<PlaceListResponse>> call = mPlacesService.getAllActivePlaces();
        call.enqueue(new Callback<List<PlaceListResponse>>() {
            @Override
            public void onResponse(Call<List<PlaceListResponse>> call, Response<List<PlaceListResponse>> response) {
                if (response.isSuccessful() && response.code() == 200){
                    getPlacesListener.onGetAllActivePlaces(response.body());
                } else {
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<List<PlaceListResponse>> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void restorePlaceByIdLocally(Long id, int position){
        Place place = mPlaceRepository.getPlaceById(id);
        if (place != null){
            mPlaceRepository.restorePlace(place);
            getPlacesListener.onRestorePlace();
        } else {
            getPlacesListener.onRestorePlaceError(position);
        }
    }

    private void restorePlaceByIdOnServer(Long id, int position){
        Call<Void> call = mPlacesService.restorePlaceById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    getPlacesListener.onRestorePlace();
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    getPlacesListener.onRestorePlaceError(position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                getPlacesListener.onRestorePlaceError(position);
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void archivePlaceLocally(PlaceListResponse placeResponse, int position){
        Place place = mPlaceRepository.getPlaceById(placeResponse.getId());
        if (place != null){
            mPlaceRepository.deletePlaceSoft(place);
            getPlacesListener.onArchivePlace(position);
        } else {
            getPlacesListener.onArchivePlaceError(placeResponse, position);
        }
    }

    private void archivePlaceOnServer(PlaceListResponse placeResponse, int position){
        Call<Void> call = mPlacesService.archivePlaceById(placeResponse.getId());
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    getPlacesListener.onArchivePlace(position);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    getPlacesListener.onArchivePlaceError(placeResponse, position);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                getPlacesListener.onArchivePlaceError(placeResponse, position);
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private PlaceListResponse convertToListPlaceListResponse(Place place){
        return mPlaceMapper.placeToPlaceListResponse(place);
    }

    public interface GetAllActivePlacesListener {
        void onGetAllActivePlaces(List<PlaceListResponse> places);

        void onRestorePlace();

        void onArchivePlace(int position);

        void onArchivePlaceError(PlaceListResponse placeListResponse, int position);

        void onRestorePlaceError(int position);
    }

}
