package com.maciejak.myplaces.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
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
import com.maciejak.myplaces.api.dto.response.AddPlaceResponse;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.AddPlaceManager;
import com.maciejak.myplaces.ui.adapters.AddPlacePhotosRecyclerViewAdapter;
import com.maciejak.myplaces.utils.FileUtils;
import com.maciejak.myplaces.utils.PermissionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddPlaceActivity extends BaseActivity implements
        AddPlaceManager.AddPlaceResponseListener,
        ServerErrorResponseListener,
        OnMapReadyCallback{

    public static final String PLACE_LAT_LNG = "AddPlaceActivity LatLng";
    public static final String ADD_PLACE_ACTIVITY_DATA = "AddPlaceActivity Data";
    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_TAKE_PHOTO= 2;
    private static final int REQUEST_CAMERA = 13;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 14;

    GoogleMap mMap;
    UiSettings mUiSettings;
    LatLng mPlaceLatLng;
    Marker mMarker;

    @BindView(R.id.add_place_title) EditText placeTitle;

    @BindView(R.id.add_place_note) EditText placeNote;

    @BindView(R.id.add_place_describe) EditText placeDescription;

    @BindView(R.id.add_place_photos_recycler_view) RecyclerView addPlacePhotosRecyclerView;

    AddPlacePhotosRecyclerViewAdapter mAddPlacePhotosRecyclerViewAdapter;

    private List<Uri> mPhotos;
    private AddPlaceManager mAddPlaceManager;
    private Uri photoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_place);
        ButterKnife.bind(this);

        mPlaceLatLng = this.getIntent().getParcelableExtra(PLACE_LAT_LNG);

        setupControls();

    }

    private void setupControls() {
        super.setupToolbar();

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.add_place_collapsing_toolbar);
        collapsingToolbar.setTitle(getString(R.string.add_place));

        mAddPlaceManager = new AddPlaceManager(this, this, this);

        mPhotos = new ArrayList<>();
        setUpRecyclerView(mPhotos);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.add_place_map);
        mapFragment.getView().setClickable(false);
        mapFragment.getMapAsync(this);
    }

    private void setUpRecyclerView(List<Uri> photos){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        addPlacePhotosRecyclerView.setLayoutManager(layoutManager);
        mAddPlacePhotosRecyclerViewAdapter = new AddPlacePhotosRecyclerViewAdapter(this, photos);
        addPlacePhotosRecyclerView.setAdapter(mAddPlacePhotosRecyclerViewAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode==RESULT_OK)
                {
                    File file = FileUtils.savePhotoToFile(data.getData(), this);
                    mAddPlaceManager.addPhoto(Uri.fromFile(file));
                    mPhotos.add(Uri.fromFile(file));
                    mAddPlacePhotosRecyclerViewAdapter.notifyDataSetChanged();
                }
                break;
            case RESULT_TAKE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    mAddPlaceManager.addPhoto(photoUri);
                    mPhotos.add(photoUri);
                    mAddPlacePhotosRecyclerViewAdapter.notifyDataSetChanged();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setAllGesturesEnabled(false);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setZoomControlsEnabled(false);

        if (mPlaceLatLng != null){
            mMarker = mMap.addMarker(new MarkerOptions()
                    .position(mPlaceLatLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 14));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_place_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.edit_place_action_done:
                addPlaceDone();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addPlaceDone() {
        mAddPlaceManager.addPlace(mPlaceLatLng,
                placeTitle.getText().toString(),
                placeNote.getText().toString(),
                placeDescription.getText().toString(),
                mPhotos);
    }

    @OnClick(R.id.add_place_add_photo_fab)
    public void addPhotoOnClick(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_photos)
                .setItems(R.array.pick_photos_array, (dialog, which) -> {
                    switch (which){
                        case 0:
                            if(!PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                                PermissionUtils.requestPermission(this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                        REQUEST_WRITE_EXTERNAL_STORAGE,
                                        R.string.write_external_storage_permission_rationale);

                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                            }
                            break;
                        case 1:
                            pickPhotoFromCamera();
                            break;
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void pickPhotoFromCamera() {
        if (!PermissionUtils.checkPermission(this, Manifest.permission.CAMERA)){
            PermissionUtils.requestPermission(this,
                    Manifest.permission.CAMERA,
                    REQUEST_CAMERA,
                    R.string.camera_permission_rationale);
        } else if(!PermissionUtils.checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            PermissionUtils.requestPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_WRITE_EXTERNAL_STORAGE,
                    R.string.write_external_storage_permission_rationale);

        } else {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                photoUri = FileUtils.createUriForTakePhoto(this);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
            }
        }
    }

    @Override
    public void onBackPressed() {
        mAddPlaceManager.trimCache(mPhotos);
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_CAMERA) {
            if (grantResults.length <= 0) {
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickPhotoFromCamera();
            } else {
                showSnackbarForPermissionDenied();
            }
        } else if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE){
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                pickPhotoFromCamera();
            } else {
                showSnackbarForPermissionDenied();
            }
        }
    }

    @Override
    public void onErrorResponse(ErrorResponse response) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onSuccessResponse() {
        Toast.makeText(this, this.getString(R.string.saved), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ADD_PLACE_ACTIVITY_DATA);
        setResult(Activity.RESULT_OK ,intent);
        this.finish();
    }

    @Override
    public void onUploadPhoto(PlacePhotoResponse placePhotoResponse) {

    }
}
