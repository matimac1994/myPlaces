package com.maciejak.myplaces.ui.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.util.PermissionUtils;

public class FavouritePlaceMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    GoogleMap mMap;
    UiSettings mUiSettings;

    String mPlaceName;
    LatLng mPlaceLatLng;

    Marker lastAddedMarker;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    public static final String SELECTED_FAVOURITE_PLACE_NAME = "FavouritePlaceMapActivity SELECTED_FAVOURITE_PLACE_NAME";
    public static final String SELECTED_FAVOURITE_PLACE_LATLNG = "FavouritePlaceMapActivity SELECTED_FAVOURITE_PLACE_LATLNG";
    public static final Integer ADD_PLACE_DONE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_place_map);

        mPlaceName = this.getIntent().getStringExtra(MainActivity.SELECTED_FAVOURITE_PLACE_NAME);
        mPlaceLatLng = this.getIntent().getParcelableExtra(MainActivity.SELECTED_FAVOURITE_PLACE_LATLNG);
        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();

        if (mPlaceName != null) {
            getSupportActionBar().setTitle(mPlaceName);
        } else {
            getSupportActionBar().setTitle(R.string.add_place);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_place_map_fragment);
        mapFragment.getMapAsync(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.favourite_map_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.favourite_place_map_action_done:
                moveToAddPlaceForm();
                break;
            case R.id.favourite_place_map_action_info:
                showInfoAlertDialog();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ADD_PLACE_DONE){
            if (resultCode == RESULT_OK){
                this.finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void moveToAddPlaceForm(){
        if (lastAddedMarker !=null) {
            Intent intent = new Intent(this, FavouritePlaceFormAddActivity.class);

            if (mPlaceName != null)
                intent.putExtra(SELECTED_FAVOURITE_PLACE_NAME, mPlaceName);

            intent.putExtra(SELECTED_FAVOURITE_PLACE_LATLNG, lastAddedMarker.getPosition());
            startActivityForResult(intent, ADD_PLACE_DONE);
        }
        else {
            Toast.makeText(this, getString(R.string.favourite_place_map_done_no_marker), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);
        enableMyLocation();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);


        if (mPlaceLatLng != null){
            addMarkerToMap(mPlaceLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPlaceLatLng, 16));
        }

    }

    private void enableMyLocation(){
        if (!checkReady())
            return;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            mMap.setMyLocationEnabled(true);
        }

    }

    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(
                    getSupportFragmentManager(), "dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        addMarkerToMap(latLng);
    }

    private void addMarkerToMap(LatLng position){
        mPlaceLatLng = position;
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(position)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.heart_red))
                .draggable(true));

        if (lastAddedMarker != null)
            lastAddedMarker.remove();
        lastAddedMarker = marker;
    }

    private void showInfoAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        builder.setTitle(R.string.favourite_place_map_info_title);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        if (mPlaceLatLng != null){
            builder.setMessage(R.string.favourite_place_map_info_message_location);
        }
        else {
            builder.setMessage(R.string.favourite_place_map_info_message_nolocation);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
