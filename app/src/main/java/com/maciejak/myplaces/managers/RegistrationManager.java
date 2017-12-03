package com.maciejak.myplaces.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.RegistrationService;
import com.maciejak.myplaces.api.dto.request.RegistrationRequest;
import com.maciejak.myplaces.api.dto.response.RegistrationResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.utils.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 28.11.2017.
 */

public class RegistrationManager extends BaseRemoteManager {

    private RegistrationResponseListener mRegistrationResponseListener;

    public RegistrationManager(Context context, RegistrationResponseListener registrationResponseListener) {
        super(context);
        this.mRegistrationResponseListener = registrationResponseListener;
    }

    public void register(String username, String email,  String password, String confirmPassword){

        RegistrationRequest registrationRequest = new RegistrationRequest(username, email, password, confirmPassword);

        RegistrationService registrationService = mRetrofit.create(RegistrationService.class);
        ServerErrorResponseListener listener = ((ServerErrorResponseListener)mContext);

        Call<RegistrationResponse> call = registrationService.register(registrationRequest);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mRegistrationResponseListener.onSuccessResponse(response.body());
                } else {
                    listener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<RegistrationResponse> call, Throwable t) {
                listener.onFailure(mContext.getString(R.string.server_error));
            }
        });
    }

    public interface RegistrationResponseListener{
        void onSuccessResponse(RegistrationResponse registrationResponse);
    }

}
