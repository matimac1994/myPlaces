package com.maciejak.myplaces.managers;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.ShowPlaceService;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;
import com.maciejak.myplaces.api.mappers.PlaceMapper;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 03.12.2017.
 */

public class ShowPlaceManager extends BaseRemoteManager {

    private ServerErrorResponseListener mServerErrorResponseListener;
    private ShowPlaceManagerListener mShowPlaceManagerListener;
    private ShowPlaceService mShowPlaceService;
    private PlaceRepository mPlaceRepository;
    private PlaceMapper mPlaceMapper = PlaceMapper.INSTANCE;

    public ShowPlaceManager(Context context, ServerErrorResponseListener serverErrorResponseListener, ShowPlaceManagerListener showPlaceManagerListener) {
        super(context);
        this.mServerErrorResponseListener = serverErrorResponseListener;
        this.mShowPlaceManagerListener = showPlaceManagerListener;
        this.mShowPlaceService = mRetrofit.create(ShowPlaceService.class);
        this.mPlaceRepository = new PlaceRepository();
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

    public void deletePlaceById(Long id){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                deletePlaceByIdLocally(id);
                break;
            case REMOTE:
                deletePlaceByIdOnServer(id);
                break;
            default:
                break;
        }
    }

    public void restorePlaceById(Long id){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                restorePlaceByIdLocally(id);
                break;
            case REMOTE:
                restorePlaceByIdOnServer(id);
                break;
            default:
                break;
        }
    }

    public void archivePlaceById(Long id){
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
                archivePlaceByIdLocally(id);
                break;
            case REMOTE:
                archivePlaceByIdOnServer(id);
                break;
            default:
                break;
        }
    }


    private void getPlaceByIdLocally(Long id){
        Place place =  mPlaceRepository.getPlaceById(id);
        if (place != null){
            PlaceResponse placeResponse = convertPlaceToPlaceResponse(place);
            mShowPlaceManagerListener.onGetPlace(placeResponse);
        } else {
            mShowPlaceManagerListener.onGetPlaceError(mContext.getString(R.string.problem_with_show_place));
        }
    }

    private void getPlaceByIdFromServer(Long id){
        Call<PlaceResponse> call = mShowPlaceService.getPlaceById(id);
        call.enqueue(new Callback<PlaceResponse>() {
            @Override
            public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mShowPlaceManagerListener.onGetPlace(response.body());
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mShowPlaceManagerListener.onGetPlaceError("Problem z wyświetleniem miejsca");
                }
            }

            @Override
            public void onFailure(Call<PlaceResponse> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void deletePlaceByIdLocally(Long id){
        Place place = mPlaceRepository.getPlaceById(id);
        if (place != null){
            mPlaceRepository.deletePlace(place);
            mShowPlaceManagerListener.onDeletePlace(true);
        } else {
            mShowPlaceManagerListener.onDeletePlace(false);
        }
    }

    private void deletePlaceByIdOnServer(Long id){
        Call<Void> call = mShowPlaceService.deletePlaceById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mShowPlaceManagerListener.onDeletePlace(true);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mShowPlaceManagerListener.onDeletePlace(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void restorePlaceByIdLocally(Long id){
        Place place = mPlaceRepository.getPlaceById(id);
        if (place != null){
            mPlaceRepository.restorePlace(place);
            mShowPlaceManagerListener.onRestorePlace(true);
        } else {
            mShowPlaceManagerListener.onRestorePlace(false);
        }
    }

    private void restorePlaceByIdOnServer(Long id){
        Call<Void> call = mShowPlaceService.restorePlaceById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mShowPlaceManagerListener.onRestorePlace(true);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mShowPlaceManagerListener.onRestorePlace(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private void archivePlaceByIdLocally(Long id){
        Place place = mPlaceRepository.getPlaceById(id);
        if (place != null){
            mPlaceRepository.deletePlaceSoft(place);
            mShowPlaceManagerListener.onArchivePlace(true);
        } else {
            mShowPlaceManagerListener.onArchivePlace(false);
        }
    }

    private void archivePlaceByIdOnServer(Long id){
        Call<Void> call = mShowPlaceService.archivePlaceById(id);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mShowPlaceManagerListener.onArchivePlace(true);
                } else if (response.errorBody() != null){
                    mServerErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                } else {
                    mShowPlaceManagerListener.onArchivePlace(false);
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                mServerErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    private PlaceResponse convertPlaceToPlaceResponse(Place place){
        return mPlaceMapper.placeToPlaceResponse(place);
    }

    public interface ShowPlaceManagerListener{
        void onGetPlace(PlaceResponse place);

        void onGetPlaceError(String message);

        void onDeletePlace(Boolean isDeleted);

        void onRestorePlace(Boolean isRestored);

        void onArchivePlace(Boolean isArchived);
    }

}
