package com.maciejak.myplaces.managers.TopPlaces;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.TopPlacesService;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.BaseRemoteManager;
import com.maciejak.myplaces.managers.ShowPlaceManager;

import java.nio.file.Path;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 14.12.2017.
 */

public class ShowTopPlaceManager extends BaseRemoteManager {

    private TopPlacesService mTopPlacesService;
    private ShowTopPlaceManagerListener listener;
    private ServerErrorResponseListener serverErrorResponseListener;

    public ShowTopPlaceManager(Context context,
                               ShowTopPlaceManagerListener listener,
                               ServerErrorResponseListener serverErrorResponseListener) {
        super(context);
        mTopPlacesService = mRetrofit.create(TopPlacesService.class);
        this.listener = listener;
        this.serverErrorResponseListener = serverErrorResponseListener;
    }

    public void getTopPlaceById(Long id){
        Call<TopPlaceResponse> call = mTopPlacesService.getTopPlaceById(id);
        call.enqueue(new Callback<TopPlaceResponse>() {
            @Override
            public void onResponse(Call<TopPlaceResponse> call, Response<TopPlaceResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    listener.onGetTopPlace(response.body());
                } else {
                    serverErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<TopPlaceResponse> call, Throwable t) {
                serverErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }


    public interface ShowTopPlaceManagerListener{
        void onGetTopPlace(TopPlaceResponse topPlace);
    }
}
