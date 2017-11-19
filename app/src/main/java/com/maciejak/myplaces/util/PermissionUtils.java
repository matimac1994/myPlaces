package com.maciejak.myplaces.util;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.maciejak.myplaces.BuildConfig;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.ui.activity.MainActivity;

import static android.app.Activity.RESULT_CANCELED;
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

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CHECK_SETTINGS) {
//            switch (resultCode) {
//                case Activity.RESULT_OK:
//                    Log.i(TAG, "User agreed to make required location settings changed");
//                    break;
//                case RESULT_CANCELED:
//                    Log.i(TAG, "User don't agreed to make required location settings changed");
//                    break;
//            }
//        }
//    }
//
//    /**
//     * Callback received when a permissions request has been completed.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
//                                           @NonNull int[] grantResults) {
//        Log.i(TAG, "onRequestPermissionResult");
//        if (requestCode == mRequestCode) {
//            if (grantResults.length <= 0) {
//                // If user interaction was interrupted, the permission request is cancelled and you
//                // receive empty arrays.
//                Log.i(TAG, "User interaction was cancelled.");
//            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                Log.i(TAG, "Permission granted, updates requested, starting location updates");
//                startLocationUpdates(this);
//            } else {
//                showSnackbar(this, R.string.permission_denied_explanation,
//                        R.string.settings, view -> {
//                            // Build intent that displays the App settings screen.
//                            Intent intent = new Intent();
//                            intent.setAction(
//                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//                            Uri uri = Uri.fromParts("package",
//                                    BuildConfig.APPLICATION_ID, null);
//                            intent.setData(uri);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                        });
//            }
//        }
//    }

























//    /**
//     * Requests the fine location permission. If a rationale with an additional explanation should
//     * be shown to the user, displays a dialog that triggers the request.
//     */
//    public static void requestPermission(AppCompatActivity activity, int requestId,
//                                         String permission, boolean finishActivity) {
//        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//            // Display a dialog with rationale.
//            PermissionUtils.RationaleDialog.newInstance(requestId, finishActivity)
//                    .show(activity.getSupportFragmentManager(), "dialog");
//        } else {
//            // Location permission has not been granted yet, request it.
//            ActivityCompat.requestPermissions(activity, new String[]{permission}, requestId);
//
//        }
//    }
//
//    /**
//     * Checks if the result contains a {@link PackageManager#PERMISSION_GRANTED} result for a
//     * permission from a runtime permissions request.
//     *
//     * @see android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback
//     */
//    public static boolean isPermissionGranted(String[] grantPermissions, int[] grantResults,
//                                              String permission) {
//        for (int i = 0; i < grantPermissions.length; i++) {
//            if (permission.equals(grantPermissions[i])) {
//                return grantResults[i] == PackageManager.PERMISSION_GRANTED;
//            }
//        }
//        return false;
//    }
//
//    /**
//     * A dialog that displays a permission denied message.
//     */
//    public static class PermissionDeniedDialog extends DialogFragment {
//
//        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";
//
//        private boolean mFinishActivity = false;
//
//        /**
//         * Creates a new instance of this dialog and optionally finishes the calling Activity
//         * when the 'Ok' button is clicked.
//         */
//        public static PermissionDeniedDialog newInstance(boolean finishActivity) {
//            Bundle arguments = new Bundle();
//            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
//
//            PermissionDeniedDialog dialog = new PermissionDeniedDialog();
//            dialog.setArguments(arguments);
//            return dialog;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            mFinishActivity = getArguments().getBoolean(ARGUMENT_FINISH_ACTIVITY);
//
//            return new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.location_permission_denied)
//                    .setPositiveButton(android.R.string.ok, null)
//                    .create();
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            super.onDismiss(dialog);
//            if (mFinishActivity) {
//                Toast.makeText(getActivity(), R.string.permission_required_toast,
//                        Toast.LENGTH_SHORT).show();
//                getActivity().finish();
//            }
//        }
//    }
//
//    /**
//     * A dialog that explains the use of the location permission and requests the necessary
//     * permission.
//     * <p>
//     * The activity should implement
//     * {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}
//     * to handle permit or denial of this permission request.
//     */
//    public static class RationaleDialog extends DialogFragment {
//
//        private static final String ARGUMENT_PERMISSION_REQUEST_CODE = "requestCode";
//
//        private static final String ARGUMENT_FINISH_ACTIVITY = "finish";
//
//        private boolean mFinishActivity = false;
//
//        /**
//         * Creates a new instance of a dialog displaying the rationale for the use of the location
//         * permission.
//         * <p>
//         * The permission is requested after clicking 'ok'.
//         *
//         * @param requestCode    Id of the request that is used to request the permission. It is
//         *                       returned to the
//         *                       {@link android.support.v4.app.ActivityCompat.OnRequestPermissionsResultCallback}.
//         * @param finishActivity Whether the calling Activity should be finished if the dialog is
//         *                       cancelled.
//         */
//        public static RationaleDialog newInstance(int requestCode, boolean finishActivity) {
//            Bundle arguments = new Bundle();
//            arguments.putInt(ARGUMENT_PERMISSION_REQUEST_CODE, requestCode);
//            arguments.putBoolean(ARGUMENT_FINISH_ACTIVITY, finishActivity);
//            RationaleDialog dialog = new RationaleDialog();
//            dialog.setArguments(arguments);
//            return dialog;
//        }
//
//        @Override
//        public Dialog onCreateDialog(Bundle savedInstanceState) {
//            Bundle arguments = getArguments();
//            final int requestCode = arguments.getInt(ARGUMENT_PERMISSION_REQUEST_CODE);
//            mFinishActivity = arguments.getBoolean(ARGUMENT_FINISH_ACTIVITY);
//
//            return new AlertDialog.Builder(getActivity())
//                    .setMessage(R.string.permission_rationale_location)
//                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // After click on Ok, request the permission.
//                            ActivityCompat.requestPermissions(getActivity(),
//                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                    requestCode);
//                            // Do not finish the Activity while requesting permission.
//                            mFinishActivity = false;
//                        }
//                    })
//                    .setNegativeButton(android.R.string.cancel, null)
//                    .create();
//        }
//
//        @Override
//        public void onDismiss(DialogInterface dialog) {
//            super.onDismiss(dialog);
//            if (mFinishActivity) {
//                Toast.makeText(getActivity(),
//                        R.string.permission_required_toast,
//                        Toast.LENGTH_SHORT)
//                        .show();
//                getActivity().finish();
//            }
//        }
//    }
}
