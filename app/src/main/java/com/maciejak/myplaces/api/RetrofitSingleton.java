package com.maciejak.myplaces.api;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Mati on 26.11.2017.
 */

public class RetrofitSingleton {
    private static Retrofit instance;

    private RetrofitSingleton(){}

    public static Retrofit getInstance(){
        if (instance == null){
            instance = new Retrofit.Builder()
                    .baseUrl(ServerConfig.SERVER_URL)
                    .addConverterFactory(JacksonConverterFactory
                            .create())
                    .build();
        }
        return instance;
    }
}
