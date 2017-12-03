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

    public PlaceListManager(Context context, ServerErrorResponseListener serverErrorResponseListener, GetAllActivePlacesListener getPlacesListener) {
        super(context);
        this.getPlacesListener = getPlacesListener;
        this.mServerErrorResponseListener = serverErrorResponseListener;
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
        List<PlaceListResponse> placesResponse = new ArrayList<>();
        for (Place place : places){
            placesResponse.add(convertToListPlaceListResponse(place));
        }
        getPlacesListener.onGetAllActivePlaces(placesResponse);
    }

    private void getPlacesFromServer(){
        PlacesService placesService = mRetrofit.create(PlacesService.class);

        Call<List<PlaceListResponse>> call = placesService.getAllActivePlaces();
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

    private PlaceListResponse convertToListPlaceListResponse(Place place){
        return mPlaceMapper.placeToPlaceListResponse(place);
    }

    public interface GetAllActivePlacesListener {
        void onGetAllActivePlaces(List<PlaceListResponse> places);
    }

}
