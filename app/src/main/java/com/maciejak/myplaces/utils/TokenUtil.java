package com.maciejak.myplaces.utils;

import android.content.SharedPreferences;

import com.maciejak.myplaces.MyPlacesApplication;

/**
 * Created by Mati on 10.12.2017.
 */

public class TokenUtil {

    private static SharedPreferences userSharedPreferences = MyPlacesApplication.getUserSharedPreferences();

    public static String getToken(){
        return userSharedPreferences.getString(Const.USER_TOKEN, "");
    }
}
