package com.maciejak.myplaces.ui.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.maps.CameraUpdate;
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
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.activity.ShowPlaceActivity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapFragment extends BaseFragment implements OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener{

    private GoogleMap mMap;
    private Location mLocation;

    List<Place> mPlaceList;
    LatLngBounds mBounds;
    List<Marker> mMarkersOnMap;
    BitmapDescriptor mMarkerIcon;
    UiSettings mUiSettings;
    PlaceRepository mPlaceRepository;

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
        setupControls(view);
        return view;
    }


    private void setupControls(View view){

        getActivity().setTitle(R.string.map);

        mMarkerIcon = BitmapDescriptorFactory.fromResource(R.drawable.heart_red);
        mMarkersOnMap = new ArrayList<>();
        mPlaceRepository = new PlaceRepository();
        FloatingActionMenu floatingActionMenu = (FloatingActionMenu) view.findViewById(R.id.add_place_menu);
        configFloatingActionMenu(getContext(), floatingActionMenu);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.my_places_map_fragment);
        mapFragment.getMapAsync(this);
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
        mMap.setOnMarkerClickListener(this);
        mMap.setOnInfoWindowClickListener(this);

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

        // TODO: 05.11.2017 Do this stuff asynchrous 
        removeOldMarkers(bounds);
        if (checkReady()){
            if (mPlaceList.size() > 0){
                LatLng latLng;
                for (Place place : mPlaceList){
                    latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    if (bounds.contains(latLng)){
                        addMarkerToMap(place);
                    }
                }
            }
        }
    }

    private void removeOldMarkers(LatLngBounds bounds) {
        for (Iterator<Marker> iterator = mMarkersOnMap.iterator(); iterator.hasNext();){
            Marker marker = iterator.next();
            if (!bounds.contains(marker.getPosition())){
                iterator.remove();
                marker.remove();
            }
        }
    }

    private void addMarkerToMap(Place place) {
        for (Marker marker : mMarkersOnMap){
            if (marker.getTag().equals(place.getId())){
                return;
            }
        }
        MarkerOptions markerOptions = addOptionsToMarker(place);
        Marker marker = mMap.addMarker(markerOptions);
        marker.setTag(place.getId());
        mMarkersOnMap.add(marker);
    }

    private MarkerOptions addOptionsToMarker(Place place){
        MarkerOptions markerOptions = new MarkerOptions();
        if (!place.getTitle().equals("")){
            markerOptions.title(place.getTitle());
        }
        else {
            markerOptions.title(getString(R.string.go_to_place));
        }

        markerOptions.position(new LatLng(place.getLatitude(), place.getLongitude()))
                .draggable(false)
                .icon(mMarkerIcon);
        return markerOptions;

    }

    @Override
    public void onStart() {
        MyPlacesApplication.getGoogleApiClientHelper().connect();
        mPlaceList = mPlaceRepository.getAllPlaces();
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
        Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
        intent.putExtra(ShowPlaceActivity.PLACE_ID, (long)marker.getTag());
        startActivity(intent);
    }
}
