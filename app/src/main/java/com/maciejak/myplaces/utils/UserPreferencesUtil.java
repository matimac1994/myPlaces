package com.maciejak.myplaces.utils;

import android.content.Intent;
import android.content.SharedPreferences;

import com.maciejak.myplaces.MyPlacesApplication;

/**
 * Created by Mati on 03.12.2017.
 */

public class UserPreferencesUtil {

    private static SharedPreferences userSharedPreferences = MyPlacesApplication.getUserSharedPreferences();

    public static UsageType checkUsageType(){
        if (userSharedPreferences.getBoolean(Const.LOCALLY_USE, false)){
            return UsageType.LOCAL;
        } else if (userSharedPreferences.getBoolean(Const.REMOTE_USE, false)) {
            return UsageType.REMOTE;
        } else {
            return UsageType.NONE;
        }
    }

    public static void setLocallyUsage(){
        userSharedPreferences.edit().putBoolean(Const.LOCALLY_USE, true).apply();
        userSharedPreferences.edit().putBoolean(Const.REMOTE_USE, false).apply();
        userSharedPreferences.edit().putString(Const.USER_TOKEN, null).apply();

    }

    public static void setRemoteUsage(String token){
        userSharedPreferences.edit().putBoolean(Const.LOCALLY_USE, false).apply();
        userSharedPreferences.edit().putBoolean(Const.REMOTE_USE, true).apply();
        userSharedPreferences.edit().putString(Const.USER_TOKEN, token).apply();
    }
}
