package com.maciejak.myplaces.api.api_services;

import com.maciejak.myplaces.api.ServerConfig;
import com.maciejak.myplaces.api.dto.request.RegistrationRequest;
import com.maciejak.myplaces.api.dto.response.RegistrationResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by Mati on 26.11.2017.
 */

public interface RegistrationService {

    @POST(ServerConfig.REGISTRATION)
    Call<RegistrationResponse> register(@Body RegistrationRequest registrationRequest);
}
