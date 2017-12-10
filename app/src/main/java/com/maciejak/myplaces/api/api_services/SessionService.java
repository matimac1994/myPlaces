package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.LoginRequest;
import com.maciejak.myplaces.api.dto.response.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Mati on 28.11.2017.
 */

public interface SessionService {

    @POST(ServerConfig.LOGIN)
    Call<LoginResponse> login(@Body LoginRequest loginRequest);

    @POST(ServerConfig.LOGOUT)
    Call<Void> logout(@Header(ServerConfig.TOKEN_NAME) String token);
}
