package com.maciejak.myplaces.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.api_services.RegistrationService;
import com.maciejak.myplaces.api.dto.request.RegistrationRequest;
import com.maciejak.myplaces.api.dto.response.RegistrationResponse;
import com.maciejak.myplaces.listeners.ServerResponseListener;
import com.maciejak.myplaces.utils.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 28.11.2017.
 */

public class RegisterManager extends BaseManager{

    public RegisterManager(Context context) {
        super(context);
    }

    public void register(String username, String email,  String password, String confirmPassword){

        RegistrationRequest registrationRequest = new RegistrationRequest(username, email, password, confirmPassword);

        RegistrationService registrationService = mRetrofit.create(RegistrationService.class);
        ServerResponseListener listener = ((ServerResponseListener)mContext);

        Call<RegistrationResponse> call = registrationService.register(registrationRequest);
        call.enqueue(new Callback<RegistrationResponse>() {
            @Override
            public void onResponse(Call<RegistrationResponse> call, Response<RegistrationResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    saveUserIdToSharedPreferences(response.body());
                    listener.onSuccessResponse(response.body());
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

    private void saveUserIdToSharedPreferences(RegistrationResponse registrationResponse) {
        if(registrationResponse != null){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Const.USER, Context.MODE_PRIVATE);
            sharedPreferences.edit().putLong(Const.USER_ID, registrationResponse.getUserId()).apply();
        }
    }

}
