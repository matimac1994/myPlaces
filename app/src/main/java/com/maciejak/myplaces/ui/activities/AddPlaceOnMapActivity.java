package com.maciejak.myplaces.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.utils.PermissionUtils;

public class AddPlaceOnMapActivity extends BaseActivity implements OnMapReadyCallback,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener {

    GoogleMap mMap;
    UiSettings mUiSettings;

    LatLng mPlaceLatLng;

    Marker lastAddedMarker;

    public static final String SELECTED_PLACE_LATLNG = "AddPlaceOnMapActivity SELECTED_PLACE_LATLNG";
    public static final Integer ADD_PLACE_DONE = 1;
    public static final String ADD_PLACE_ON_MAP_DATA = "AddPlaceOnMapActivity Data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place_on_map);

        mPlaceLatLng = this.getIntent().getParcelableExtra(SELECTED_PLACE_LATLNG);
        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();

        getSupportActionBar().setTitle(R.string.add_place);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.add_place_on_map_fragment);
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
                Intent intent = new Intent(ADD_PLACE_ON_MAP_DATA);
                setResult(Activity.RESULT_OK ,intent);
                this.finish();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void moveToAddPlaceForm(){
        if (lastAddedMarker !=null) {
            Intent intent = new Intent(this, AddPlaceActivity.class);
            intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, lastAddedMarker.getPosition());
            startActivityForResult(intent, ADD_PLACE_DONE);
        }
        else {
            Toast.makeText(this, getString(R.string.favourite_place_map_done_no_marker), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        mMap.setOnMarkerDragListener(this);
        mMap.setOnMapLongClickListener(this);


        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(true);

        if (PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)){
            mMap.setMyLocationEnabled(true);
        }

        if (mPlaceLatLng != null){
            addMarkerToMap(mPlaceLatLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mPlaceLatLng, 16));
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
        builder.setTitle(R.string.add_place_on_map_info_title);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        if (mPlaceLatLng != null){
            builder.setMessage(R.string.add_place_on_map_info_message_location);
        }
        else {
            builder.setMessage(R.string.add_place_on_map_info_message_nolocation);
        }

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
