package com.maciejak.myplaces.managers.TopPlaces;

import android.content.Context;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.TopPlacesService;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponseList;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.BaseRemoteManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 14.12.2017.
 */

public class TopPlacesFragmentManager extends BaseRemoteManager {

    private TopPlacesService mTopPlacesService;
    private TopPlacesFragmentManagerListener listener;
    private ServerErrorResponseListener serverErrorResponseListener;

    public TopPlacesFragmentManager(Context context, ServerErrorResponseListener serverErrorResponseListener, TopPlacesFragmentManagerListener listener) {
        super(context);
        this.mTopPlacesService = mRetrofit.create(TopPlacesService.class);
        this.serverErrorResponseListener = serverErrorResponseListener;
        this.listener = listener;
    }

    public void getTopPlaces(){
        Call<List<TopPlaceResponseList>> call = mTopPlacesService.getTopPlaces();
        call.enqueue(new Callback<List<TopPlaceResponseList>>() {
            @Override
            public void onResponse(Call<List<TopPlaceResponseList>> call, Response<List<TopPlaceResponseList>> response) {
                if (response.isSuccessful() && response.code() == 200){
                    listener.onGetTopPlaces(response.body());
                } else {
                    serverErrorResponseListener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<List<TopPlaceResponseList>> call, Throwable t) {
                serverErrorResponseListener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    public interface TopPlacesFragmentManagerListener{
        void onGetTopPlaces(List<TopPlaceResponseList> topPlaces);
    }
}
