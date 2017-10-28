package com.maciejak.myplaces.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.maciejak.myplaces.R;

public class GpsLocationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Toast.makeText(context, "in android.location.PROVIDERS_CHANGED",
                    Toast.LENGTH_SHORT).show();
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if( !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ) {
                showAlertGpsOff(context);
            }else{
                Toast.makeText(context, "GPS Włączony ;)", Toast.LENGTH_SHORT).show();
            }
        }


    }

    private void showAlertGpsOff(Context context){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.dialog_gps_is_off)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog =  builder.create();
        dialog.show();

    }
}
