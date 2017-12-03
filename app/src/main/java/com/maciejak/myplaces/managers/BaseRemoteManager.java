package com.maciejak.myplaces.managers;

import android.content.Context;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maciejak.myplaces.api.RetrofitSingleton;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Mati on 28.11.2017.
 */

public class BaseRemoteManager extends BaseManager {

    protected Retrofit mRetrofit;
    protected ObjectMapper mapper;

    public BaseRemoteManager(Context context) {
        super(context);
        this.mRetrofit = RetrofitSingleton.getInstance();
        this.mapper = new ObjectMapper();
    }

    protected ErrorResponse parseErrorResponseToObject(Response response) {
        ErrorResponse errorResponse = new ErrorResponse();
        if (response != null){
            if (response.errorBody() != null){
                try {
                    errorResponse = mapper.readValue(response.errorBody().string(), ErrorResponse.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return errorResponse;
    }
}
