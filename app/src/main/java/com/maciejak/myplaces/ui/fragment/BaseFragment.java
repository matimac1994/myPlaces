package com.maciejak.myplaces.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.MyPlacesApplication;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.helper.GoogleApiClientHelper;
import com.maciejak.myplaces.ui.activity.AddPlaceActivity;
import com.maciejak.myplaces.ui.activity.AddPlaceOnMapActivity;
import com.maciejak.myplaces.util.Const;
import com.maciejak.myplaces.util.PermissionUtils;

import static android.app.Activity.RESULT_OK;
import static com.maciejak.myplaces.ui.activity.AddPlaceOnMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG;


/**
 * Created by Mati on 21.10.2017.
 */

public class BaseFragment extends Fragment implements PlaceSelectionListener, GoogleApiClientHelper.ConnectionListener {

    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int ADD_PLACE_DONE = 2;

    FloatingActionMenu mFloatingActionMenu;
    FloatingActionButton mAddPlaceFromMyLocationActionButton;
    FloatingActionButton mAddPlaceFromMapActionButton;
    FloatingActionButton mAddPlaceFromSearchActionButton;

    FusedLocationProviderClient mFusedLocationProviderClient;
    Location mLocation;
    SharedPreferences mSharedPreferences;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        mSharedPreferences = context.getSharedPreferences(Const.LOCATION, Context.MODE_PRIVATE);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFloatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.add_place_menu);
        configFloatingActionMenu(getContext(), mFloatingActionMenu);
    }

    protected void configFloatingActionMenu(Context context, FloatingActionMenu floatingActionMenu) {
        mFloatingActionMenu = floatingActionMenu;

        mAddPlaceFromMyLocationActionButton = configFromMyLocationActionButton(context);
        mAddPlaceFromMapActionButton = configFromMapActionButton(context);
        mAddPlaceFromSearchActionButton = configFromSearchActionButton(context);

        mFloatingActionMenu.addMenuButton(mAddPlaceFromSearchActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMapActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMyLocationActionButton);

        mFloatingActionMenu.setClosedOnTouchOutside(true);

    }

    protected FloatingActionButton configFromMyLocationActionButton(Context context) {
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_my_location_button_label));
        fabButton.setImageResource(R.drawable.ic_my_location_white_24dp);
        fabButton.setOnClickListener(v -> {
            Intent intent;
            getMyLocation();
            if (mLocation != null){
                intent = new Intent(context, AddPlaceActivity.class);
                intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, new LatLng(mLocation.getLongitude(), mLocation.getLatitude()));
            }
            else {
                if (mSharedPreferences.contains(Const.LONGITUDE) && mSharedPreferences.contains(Const.LATITUDE)){
                    Toast.makeText(context, R.string.last_known_location, Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, AddPlaceActivity.class);
                    Double latitude = Double.parseDouble(mSharedPreferences.getString(Const.LATITUDE, "0"));
                    Double longitude = Double.parseDouble(mSharedPreferences.getString(Const.LONGITUDE, "0"));
                    intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, new LatLng(latitude, longitude));
                }
                else {
                    Toast.makeText(context, R.string.cannot_find_location_alert, Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, AddPlaceOnMapActivity.class);
                }
            }
            mFloatingActionMenu.close(true);
            startActivityForResult(intent, ADD_PLACE_DONE);
        });
        return fabButton;
    }

    protected FloatingActionButton configFromMapActionButton(final Context context){
        FloatingActionButton fabButton  = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_map_button_label));
        fabButton.setImageResource(R.drawable.ic_add_location_white_24dp);
        fabButton.setOnClickListener(v -> {
            mFloatingActionMenu.close(true);
            Intent intent = new Intent(context, AddPlaceOnMapActivity.class);
            startActivityForResult(intent, ADD_PLACE_DONE);
        });
        return fabButton;
    }

    protected FloatingActionButton configFromSearchActionButton(Context context){
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_search_button_label));
        fabButton.setImageResource(R.drawable.ic_search_white_24dp);
        fabButton.setOnClickListener(v -> {
            try {
                mFloatingActionMenu.close(true);
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
        return fabButton;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocation = getMyLocation();
    }


    @Override
    public void onPlaceSelected(Place place) {
        Intent intent = new Intent(getContext(), AddPlaceOnMapActivity.class);
        intent.putExtra(AddPlaceOnMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG, place.getLatLng());
        startActivityForResult(intent, ADD_PLACE_DONE);
    }

    @Override
    public void onError(Status status) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                this.onError(status);
            }
        }
        else if (requestCode == ADD_PLACE_DONE){
            if (resultCode == RESULT_OK){
                actionAfterAddPlaceDone(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void actionAfterAddPlaceDone(Intent data) {
    }

    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    this.getFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission((AppCompatActivity)getActivity(), requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    public Location getMyLocation() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            mFusedLocationProviderClient.flushLocations();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), location -> {
                        if (location != null) {
                            mLocation = location;
                        }
                    });
        }
        return mLocation;
    }
}
