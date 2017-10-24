package com.maciejak.myplaces;

import android.app.Application;

import com.maciejak.myplaces.helpers.GoogleApiClientHelper;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Mati on 15.10.2017.
 */

public class MyPlacesApplication extends Application {

    private static MyPlacesApplication myPlacesApplication;
    private GoogleApiClientHelper mGoogleApiClientHelper;

    @Override
    public void onCreate() {
        super.onCreate();

        myPlacesApplication = this;
        mGoogleApiClientHelper = new GoogleApiClientHelper(this);
        FlowManager.init(this);
    }

    public static synchronized MyPlacesApplication getInstance(){return myPlacesApplication;}

    public GoogleApiClientHelper getGoogleApiClientHelperInstance() {
        return this.mGoogleApiClientHelper;
    }
    public static GoogleApiClientHelper getGoogleApiClientHelper() {
        return getInstance().getGoogleApiClientHelperInstance();
    }
}
