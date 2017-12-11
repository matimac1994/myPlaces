package com.maciejak.myplaces.api;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.utils.LogoutHandler;
import com.maciejak.myplaces.utils.UsageType;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Mati on 26.11.2017.
 */

public class RetrofitSingleton {
    private static Retrofit instance;

    private RetrofitSingleton(){}

    public static Retrofit getInstance(final Context context){
        if (instance == null){
            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
            clientBuilder.
                    addInterceptor(chain -> {
                        Request request = chain.request();
                        Response response = chain.proceed(request);
                        if (response.code() == 403) {
                            if (UserPreferencesUtil.checkUsageType().equals(UsageType.REMOTE)){
                                handleLogout(context);
                            }
                        }
                        return response;
                    });
            instance = new Retrofit.Builder()
                    .baseUrl(ServerConfig.SERVER_URL)
                    .addConverterFactory(JacksonConverterFactory
                            .create())
                    .client(clientBuilder.build())
                    .build();
        }

        return instance;
    }

    private static void handleLogout(Context context) {
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = () -> LogoutHandler.logout(context, context.getString(R.string.session_expired));
        mainHandler.post(myRunnable);

    }
}
