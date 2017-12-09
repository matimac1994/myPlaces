package com.maciejak.myplaces.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.maciejak.myplaces.api.dto.response.PlaceMapResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.MapFragmentManager;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.ui.activities.ShowPlaceActivity;
import com.maciejak.myplaces.ui.dialogs.ErrorDialog;
import com.maciejak.myplaces.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapFragment extends BaseFragment implements
        ServerErrorResponseListener,
        MapFragmentManager.GetListPlaceMapResponseListener,
        OnMapReadyCallback,
        GoogleMap.OnCameraIdleListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnInfoWindowClickListener {

    private GoogleMap mMap;

    List<PlaceMapResponse> mPlaceList = new ArrayList<>();
    LatLngBounds mBounds;
    List<Marker> mMarkersOnMap;
    UiSettings mUiSettings;
    PlaceRepository mPlaceRepository;
    MapFragmentManager mMapFragmentManager;

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
        mMapFragmentManager = new MapFragmentManager(mContext, this, this);

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

        if (PermissionUtils.checkPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionUtils.checkPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION)){
            mMap.setMyLocationEnabled(true);
        }

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
                for (PlaceMapResponse place : mPlaceList) {
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

    private void addMarkerToMap(PlaceMapResponse place) {
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

    private MarkerOptions addOptionsToMarker(PlaceMapResponse place) {
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
        mMapFragmentManager.getPlaces();
//        mPlaceList = mPlaceRepository.getAllVisiblePlaces();
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
        Intent intent = new Intent(getContext(), ShowPlaceActivity.class);
        intent.putExtra(ShowPlaceActivity.PLACE_ID, (long)marker.getTag());
        startActivity(intent);
    }

    @Override
    public void onGetListOfPlaceMapResponse(List<PlaceMapResponse> places) {
        mPlaceList = places;
        refreshMarkersOnMap();
    }

    @Override
    public void onErrorResponse(ErrorResponse response) {
        String message;
        if (response.getErrors() != null) {
            message = response.getErrors().get(0).getDefaultMessage();
        }else {
            message = response.getMessage();
        }
        ErrorDialog errorDialog = new ErrorDialog(mContext, message);
        errorDialog.show();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }
}
