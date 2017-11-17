package com.maciejak.myplaces.ui.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejak.myplaces.MyPlacesApplication;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.helper.GoogleApiClientHelper;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;
import com.maciejak.myplaces.util.PermissionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    List<Place> mPlaceList;
    LatLngBounds mBounds;
    List<Marker> mMarkersOnMap;
    UiSettings mUiSettings;
    PlaceRepository mPlaceRepository;

    public MapFragment() {}

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        setupControls(view);
        return view;
    }


    private void setupControls(View view) {

        getActivity().setTitle(R.string.map);

        mMarkersOnMap = new ArrayList<>();
        mPlaceRepository = new PlaceRepository();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.my_places_map_fragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();

        mMap.setOnCameraIdleListener(this);
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

        //Permission is checked in BaseActivity
        mMap.setMyLocationEnabled(true);

        refreshMarkersOnMap();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));
    }

    @Override
    public void onResume() {
        super.onResume();
        removeAllMarkers();
        refreshMarkersOnMap();
    }

    private void removeAllMarkers(){
        for (Marker marker : mMarkersOnMap){
            marker.remove();
        }
        mMarkersOnMap.clear();
    }

    private void refreshMarkersOnMap() {
        if (!checkReady())
            return;
        mBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        // TODO: 05.11.2017 Do this stuff asynchrous 
        removeOldMarkers(mBounds);
        if (checkReady()) {
            if (mPlaceList.size() > 0) {
                LatLng latLng;
                for (Place place : mPlaceList) {
                    latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    if (mBounds.contains(latLng)) {
                        addMarkerToMap(place);
                    }
                }
            }
        }
    }

    private void removeOldMarkers(LatLngBounds bounds) {
        for (Iterator<Marker> iterator = mMarkersOnMap.iterator(); iterator.hasNext(); ) {
            Marker marker = iterator.next();
            if (!bounds.contains(marker.getPosition())) {
                iterator.remove();
                marker.remove();
            }
        }
    }

    private void addMarkerToMap(Place place) {
        for (Marker marker : mMarkersOnMap) {
            if (marker.getTag().equals(place.getId())) {
                return;
            }
        }
        MarkerOptions markerOptions = addOptionsToMarker(place);
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(place.getId());
        mMarkersOnMap.add(marker);
    }

    private MarkerOptions addOptionsToMarker(Place place) {
        MarkerOptions markerOptions = new MarkerOptions();
        if (!place.getTitle().equals("")) {
            markerOptions.title(place.getTitle());
        } else {
            markerOptions.title(getString(R.string.go_to_place));
        }

        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()))
                .draggable(false);
        return markerOptions;

    }

    @Override
    public void onStart() {
        MyPlacesApplication.getGoogleApiClientHelper().connect();
        mPlaceList = mPlaceRepository.getAllVisiblePlaces();
        super.onStart();
    }

    @Override
    public void onStop() {
        MyPlacesApplication.getGoogleApiClientHelper().disconnect();
        super.onStop();
    }

    private boolean checkReady() {
        return (mMap != null);
    }

    @Override
    public void onCameraIdle() {
        refreshMarkersOnMap();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
        marker.showInfoWindow();
        return true;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.hideInfoWindow();
        Place place = mPlaceRepository.getPlaceById((long)marker.getTag());
        if (place != null){
            Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
            intent.putExtra(ShowPlaceActivity.PLACE_ID, place.getId());
            startActivity(intent);
        }
    }
}
