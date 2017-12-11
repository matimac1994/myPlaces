package com.maciejak.myplaces.utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.maciejak.myplaces.ui.activities.LoginActivity;

/**
 * Created by Mati on 11.12.2017.
 */

public class LogoutHandler {

    public static void logout(Context context, String reason){
        Toast.makeText(context, reason, Toast.LENGTH_SHORT).show();
        UserPreferencesUtil.clearUsage();
        Intent intent = new Intent(context, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }
}
