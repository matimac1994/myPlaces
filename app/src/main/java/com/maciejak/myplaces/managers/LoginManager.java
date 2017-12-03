package com.maciejak.myplaces.managers;

import android.content.Context;
import android.content.SharedPreferences;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.request.LoginRequest;
import com.maciejak.myplaces.api.dto.response.LoginResponse;
import com.maciejak.myplaces.api.api_services.SessionService;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.utils.Const;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Mati on 28.11.2017.
 */

public class LoginManager extends BaseRemoteManager {

    private LoginResponseListener mLoginResponseListener;

    public LoginManager(Context context, LoginResponseListener loginResponseListener){
        super(context);
        this.mContext = context;
        this.mLoginResponseListener = loginResponseListener;
    }

    public void login(String username, String password){
        LoginRequest loginRequest = new LoginRequest(username, password);
        SessionService sessionService = mRetrofit.create(SessionService.class);
        ServerErrorResponseListener listener = ((ServerErrorResponseListener)mContext);
        Call<LoginResponse> call = sessionService.login(loginRequest);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.code() == 200){
                    mLoginResponseListener.onSuccessResponse(response.body());
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


    public interface LoginResponseListener{
        void onSuccessResponse(LoginResponse loginResponse);
    }

}
