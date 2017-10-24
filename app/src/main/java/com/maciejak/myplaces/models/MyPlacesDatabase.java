package com.maciejak.myplaces.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Mati on 22.10.2017.
 */

@Database(name = MyPlacesDatabase.NAME, version = MyPlacesDatabase.VERSION)
public class MyPlacesDatabase {

    public static final String NAME = "MyPlacesDatabase";
    public static final int VERSION = 1;

}
