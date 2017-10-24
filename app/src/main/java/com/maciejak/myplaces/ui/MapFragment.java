package com.maciejak.myplaces.ui;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.maciejak.myplaces.MyPlacesApplication;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.models.Place;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener{

    private GoogleMap mMap;
    private Location mLocation;

    List<Place> mPlaceList;
    LatLngBounds mBounds;
    List<Marker> mMarkersOnMap;
    BitmapDescriptor mMarkerIcon;
    UiSettings mUiSettings;

    public MapFragment() {
        // Required empty public constructor
    }

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

        mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.heart_red);
        mMarkersOnMap = new ArrayList<>();

        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.add_place_menu);
        configFloatingActionMenu(getContext(), floatingActionMenu);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.my_places_map_fragment);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mMap.setOnCameraIdleListener(this);
        mBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        refreshMarkersOnMap(mBounds);
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mBounds, 50));
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshMarkersOnMap(mBounds);
    }

    private void refreshMarkersOnMap(LatLngBounds bounds){
        removeOldMarkers();
        if (checkReady()){
            if (mPlaceList.size() > 0){
                LatLng latLng;
                for (Place place : mPlaceList){
                    latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    if (bounds.contains(latLng)){
                        addMarkerToMap(latLng);
                    }
                }
            }
        }
    }

    private void removeOldMarkers() {
        for (Marker marker : mMarkersOnMap){
            marker.remove();
        }
        mMarkersOnMap.clear();
    }

    private void addMarkerToMap(LatLng latLng) {
        Marker marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .icon(mMarkerIcon));
        mMarkersOnMap.add(marker);
    }


    @Override
    public void onStart() {
        MyPlacesApplication.getGoogleApiClientHelper().connect();
        mPlaceList = SQLite.select().from(Place.class).queryList();
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
        mBounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        refreshMarkersOnMap(mBounds);
        Log.i("Tag", "onCameraIdle");
    }

}
