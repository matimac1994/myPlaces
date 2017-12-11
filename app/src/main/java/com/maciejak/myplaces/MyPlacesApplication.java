package com.maciejak.myplaces;

import android.app.Application;
import android.content.SharedPreferences;

import com.maciejak.myplaces.helpers.GoogleApiClientHelper;
import com.maciejak.myplaces.utils.Const;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Mati on 15.10.2017.
 */

public class MyPlacesApplication extends Application {

    private static MyPlacesApplication myPlacesApplication;
    private GoogleApiClientHelper mGoogleApiClientHelper;
    private static SharedPreferences userSharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        myPlacesApplication = this;
        mGoogleApiClientHelper = new GoogleApiClientHelper(this);
        FlowManager.init(this);

        userSharedPreferences = this.getSharedPreferences(Const.USER, MODE_PRIVATE);
    }

    public static synchronized MyPlacesApplication getInstance(){return myPlacesApplication;}

    public GoogleApiClientHelper getGoogleApiClientHelperInstance() {
        return this.mGoogleApiClientHelper;
    }
    public static GoogleApiClientHelper getGoogleApiClientHelper() {
        return getInstance().getGoogleApiClientHelperInstance();
    }

    public static SharedPreferences getUserSharedPreferences() {
        return userSharedPreferences;
    }
}
