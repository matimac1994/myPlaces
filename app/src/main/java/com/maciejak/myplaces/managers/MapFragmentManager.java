package com.maciejak.myplaces.managers;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.PlacesService;
import com.maciejak.myplaces.api.dto.response.PlaceMapResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.TokenUtil;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 03.12.2017.
 */

public class MapFragmentManager extends BaseRemoteManager {

    private GetListPlaceMapResponseListener mGetListPlaceMapResponseListener;
    private ServerErrorResponseListener mServerErrorResponseListener;
    private PlaceRepository mPlaceRepository;
    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;
    private PlacesService mPlacesService;

    public MapFragmentManager(Context context, ServerErrorResponseListener serverErrorResponseListener, GetListPlaceMapResponseListener getListPlaceMapResponseListener) {
        super(context);
        this.mGetListPlaceMapResponseListener = getListPlaceMapResponseListener;
        this.mServerErrorResponseListener = serverErrorResponseListener;
        mPlaceRepository = new PlaceRepository();
        mPlacesService = mRetrofit.create(PlacesService.class);
    }

    public void getPlaces() {
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
        List<PlaceMapResponse> placesForMap = new ArrayList<>();
        for (Place place : places){
            placesForMap.add(convertToPlaceMapResponse(place));
        }
        mGetListPlaceMapResponseListener.onGetListOfPlaceMapResponse(placesForMap);
    }

    private void getPlacesFromServer(){
        Call<List<PlaceMapResponse>> call = mPlacesService.getActivePlacesOnMap(TokenUtil.getToken());
        call.enqueue(new Callback<List<PlaceMapResponse>>() {
            @Override
            public void onResponse(Call<List<PlaceMapResponse>> call, Response<List<PlaceMapResponse>> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mGetListPlaceMapResponseListener.onGetListOfPlaceMapResponse(response.body());
                }else {
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<List<PlaceMapResponse>> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private PlaceMapResponse convertToPlaceMapResponse(Place place){
        return mPlaceMapper.placeToMapPlaceResponse(place);
    }


    public interface GetListPlaceMapResponseListener{
        void onGetListOfPlaceMapResponse(List<PlaceMapResponse> places);
    }

}
