package com.maciejak.myplaces.managers;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.ArchivePlacesService;
import com.maciejak.myplaces.api.dto.request.PlaceIdsRequest;
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
 * Created by Mati on 03.12.2017.
 */

public class ArchiveManager extends BaseRemoteManager {

    private ServerErrorResponseListener mServerErrorResponseListener;
    private ArchiveManagerListener mArchiveManagerListener;
    private PlaceRepository mPlaceRepository = new PlaceRepository();
    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private ArchivePlacesService mArchivePlacesService;

    public ArchiveManager(Context context, ServerErrorResponseListener serverErrorResponseListener, ArchiveManagerListener archiveManagerListener) {
        super(context);
        mServerErrorResponseListener = serverErrorResponseListener;
        mArchiveManagerListener = archiveManagerListener;
        this.mPlaceRepository = new PlaceRepository();
        mArchivePlacesService = mRetrofit.create(ArchivePlacesService.class);
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

    public void restorePlaces(List<Long> placeIds) {
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                restorePlacesLocally(placeIds);
                break;
            case REMOTE:
                restorePlacesByFromServer(placeIds);
                break;
            default:
                break;
        }
    }

    public void deletePlaces(List<Long> placeIds) {
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                deletePlacesLocally(placeIds);
                break;
            case REMOTE:
                deletePlacesByFromServer(placeIds);
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
        mArchiveManagerListener.onGetPlaces(placesResponse);
    }

    private void getPlacesFromServer(){
        Call<List<PlaceListResponse>> call = mArchivePlacesService.getArchivePlaces();
        call.enqueue(new Callback<List<PlaceListResponse>>() {
            @Override
            public void onResponse(Call<List<PlaceListResponse>> call, Response<List<PlaceListResponse>> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mArchiveManagerListener.onGetPlaces(response.body());
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

    private void restorePlacesLocally(List<Long> placeIds){
        for (Long id : placeIds){
            try {
                mPlaceRepository.restorePlaceById(id);
            } catch (Exception e) {
                mArchiveManagerListener.onError(mContext.getString(R.string.problem_with_restore_places));
                return;
            }
        }

        mArchiveManagerListener.onRestorePlaces(true);

    }

    private void restorePlacesByFromServer(List<Long> placeIds){
        PlaceIdsRequest placeIdsRequest = new PlaceIdsRequest(placeIds);

        Call<Void> call = mArchivePlacesService.restorePlaces(placeIdsRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mArchiveManagerListener.onRestorePlaces(true);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mArchiveManagerListener.onError(mContext.getString(R.string.problem_with_restore_places));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void deletePlacesLocally(List<Long> placeIds){
        for (Long id : placeIds){
            try {
                mPlaceRepository.deletePlaceById(id);
            } catch (Exception e) {
                mArchiveManagerListener.onError(mContext.getString(R.string.problem_with_delete_places));
                return;
            }
        }

        mArchiveManagerListener.onDeletePlaces(true);
    }

    private void deletePlacesByFromServer(List<Long> placeIds){
        PlaceIdsRequest placeIdsRequest = new PlaceIdsRequest(placeIds);

        Call<Void> call = mArchivePlacesService.deletePlaces(placeIdsRequest);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mArchiveManagerListener.onDeletePlaces(true);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mArchiveManagerListener.onError(mContext.getString(R.string.problem_with_delete_places));
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private PlaceListResponse convertToListPlaceListResponse(Place place){
        return mPlaceMapper.placeToPlaceListResponse(place);
    }

    public interface ArchiveManagerListener{
        void onGetPlaces(List<PlaceListResponse> places);
        void onDeletePlaces(Boolean isDeleted);
        void onRestorePlaces(Boolean isRestored);
        void onError(String message);
    }

}
