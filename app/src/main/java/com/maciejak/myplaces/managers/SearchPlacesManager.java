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
 * Created by Mati on 09.12.2017.
 */

public class SearchPlacesManager extends BaseRemoteManager {

    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlaceRepository mPlaceRepository;
    private PlacesService mPlacesService;
    private SearchPlacesManagerListener mListener;
    private ServerErrorResponseListener mServerErrorResponseListener;

    public SearchPlacesManager(Context context, ServerErrorResponseListener serverErrorResponseListener, SearchPlacesManagerListener searchPlacesManagerListener) {
        super(context);
        mPlacesService = mRetrofit.create(PlacesService.class);
        mPlaceRepository = new PlaceRepository();
        mListener = searchPlacesManagerListener;
        mServerErrorResponseListener = serverErrorResponseListener;
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

    private void getPlacesLocally(){
        List<Place> places = mPlaceRepository.getAllVisiblePlaces();
        List<PlaceListResponse> placesResponse = convertPlaceToPlaceListResponse(places);
        mListener.onGetPlaces(placesResponse);
    }

    private void getPlacesFromServer(){
        Call<List<PlaceListResponse>> call = mPlacesService.getAllActivePlaces();
        call.enqueue(new Callback<List<PlaceListResponse>>() {
            @Override
            public void onResponse(Call<List<PlaceListResponse>> call, Response<List<PlaceListResponse>> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mListener.onGetPlaces(response.body());
                }
                else {
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<List<PlaceListResponse>> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private List<PlaceListResponse> convertPlaceToPlaceListResponse(List<Place> places){
        List<PlaceListResponse> placesResponse = new ArrayList<>();
        for (Place place : places){
            placesResponse.add(mPlaceMapper.placeToPlaceListResponse(place));
        }
        return placesResponse;
    }

    public interface SearchPlacesManagerListener{
        void onGetPlaces(List<PlaceListResponse> places);
    }
}
