package com.maciejak.myplaces.utils;


import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.ui.activities.MainActivity;

import static android.content.ContentValues.TAG;


/**
 * Created by Mati on 21.10.2017.
 */

public class PermissionUtils {

    public static boolean checkPermission(Context context, String permission){
        return (ActivityCompat.checkSelfPermission(context,
                permission) == PackageManager.PERMISSION_GRANTED);
    }

    public static void showSnackbar(AppCompatActivity activity, final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar snackbar;
        if (activity instanceof MainActivity){
            snackbar = Snackbar.make(
                    activity.findViewById(R.id.coordinatorLayout),
                    activity.getString(mainTextStringId),
                    Snackbar.LENGTH_LONG);
        }
        else {
            snackbar = Snackbar.make(
                    activity.findViewById(android.R.id.content),
                    activity.getString(mainTextStringId),
                    Snackbar.LENGTH_LONG);
        }
        snackbar.setAction(activity.getString(actionStringId), listener);
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public static void requestPermission(AppCompatActivity activity, String permission, int requestCode, int snackBarMessageId) {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        permission);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(activity, snackBarMessageId,
                    android.R.string.ok, view -> {
                        // Request permission
                        ActivityCompat.requestPermissions(activity,
                                new String[]{permission},
                                requestCode);
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission},
                    requestCode);
        }
    }

}
