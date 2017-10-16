package com.maciejak.myplaces;

import android.app.Application;

/**
 * Created by Mati on 15.10.2017.
 */

public class MyPlacesApplication extends Application {

    MyPlacesApplication myPlacesApplication;

    @Override
    public void onCreate() {
        super.onCreate();

        myPlacesApplication = this;
    }

    public MyPlacesApplication getInstance(){return myPlacesApplication;}
}
