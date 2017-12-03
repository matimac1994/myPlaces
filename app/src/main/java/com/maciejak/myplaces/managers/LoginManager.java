package com.maciejak.myplaces.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.request.LoginRequest;
import com.maciejak.myplaces.api.dto.response.LoginResponse;
import com.maciejak.myplaces.api.api_services.SessionService;
import com.maciejak.myplaces.listeners.ServerResponseListener;
import com.maciejak.myplaces.utils.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 28.11.2017.
 */

public class LoginManager extends BaseManager{

    public LoginManager(Context context){
        super(context);
        this.mContext = context;
    }

    public void login(String username, String password){
        LoginRequest loginRequest = new LoginRequest(username, password);
        SessionService sessionService = mRetrofit.create(SessionService.class);
        ServerResponseListener listener = ((ServerResponseListener)mContext);
        Call<LoginResponse> call = sessionService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    saveTokenToSharedPreferences(response.body());
                    listener.onSuccessResponse(response.body());
                } else{
                    listener.onErrorResponse(parseErrorResponseToObject(response));
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                listener.onFailure(mContext.getString(R.string.server_error));
            }
        });

    }

    private void saveTokenToSharedPreferences(LoginResponse loginResponse){
        if(loginResponse != null){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences(Const.USER, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Const.USER_TOKEN, loginResponse.getToken()).apply();
        }
    }

}
