package com.maciejak.myplaces.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
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
import com.maciejak.myplaces.repository.PlaceRepository;
import com.maciejak.myplaces.ui.adapter.AddPlacePhotosRecyclerViewAdapter;
import com.maciejak.myplaces.util.RealPathUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavouritePlaceFormAddActivity extends BaseActivity implements OnMapReadyCallback{

    GoogleMap mMap;
    UiSettings mUiSettings;

    String mPlaceName;
    LatLng mPlaceLatLng;

    Marker mMarker;

    @BindView(R.id.add_place_form_photos_recycler_view)
    RecyclerView addPlaceFormPhotosRecyclerView;

    @BindView(R.id.add_place_form_title)
    EditText placeTitle;

    @BindView(R.id.add_place_form_note)
    EditText placeNote;

    @BindView(R.id.add_place_form_describe)
    EditText placeDescription;

    AddPlacePhotosRecyclerViewAdapter mAddPlacePhotosRecyclerViewAdapter;

    List<Uri> mPhotos;
    Uri mapPhoto;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_TAKE_PHOTO= 2;
    private PlaceRepository mPlaceRepository;
    private String mCurrentPhotoPath;
    private List<String> photoPaths;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_place_form_add);
        ButterKnife.bind(this);

        mPlaceName = this.getIntent().getStringExtra(FavouritePlaceMapActivity.SELECTED_FAVOURITE_PLACE_NAME);
        mPlaceLatLng = this.getIntent().getParcelableExtra(FavouritePlaceMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG);

        setupControls();

    }

    private void setupControls() {
        super.setupToolbar();

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.favourite_place_form_add_collapsing_toolbar);
        if (mPlaceName != null)
            collapsingToolbar.setTitle(mPlaceName);
        else
            collapsingToolbar.setTitle("Dodaj miejsce");


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlaceDone();
            }
        });

        FloatingActionButton addPhotoFab = (FloatingActionButton) findViewById(R.id.add_photo_fab);
        addPhotoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickPhotoDialog();
            }
        });

        //config RecyclerView
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
            addPlaceFormPhotosRecyclerView.setLayoutManager(layoutManager);
        mPhotos = new ArrayList<>();
        photoPaths = new ArrayList<>();

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.add_place_form_map);
        mapFragment.getView().setClickable(false);
        mapFragment.getMapAsync(this);
    }

    private void showPickPhotoDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_photos)
                .setItems(R.array.pick_photos_array, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                intent.setType("image/*");
                                startActivityForResult(intent, RESULT_LOAD_IMAGE);
                                break;
                            case 1:
                                pickPhotoFromCamera();
                                break;
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode==RESULT_OK)
                {
                    java.io.OutputStream os;
                    try {
                        Uri selectedImg = data.getData();
                        File file = createImageFile();
                        Bitmap bitmapPhoto = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImg);
                        os = new FileOutputStream(file);
                        bitmapPhoto.compress(Bitmap.CompressFormat.JPEG, 75, os);
                        os.flush();
                        os.close();
                        mPhotos.add(Uri.fromFile(file));
                        populateRecyclerView(mPhotos);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;
            case RESULT_TAKE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    mPhotos.add(photoURI);
                    populateRecyclerView(mPhotos);

                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void populateRecyclerView(List<Uri> photos){
        mAddPlacePhotosRecyclerViewAdapter = new AddPlacePhotosRecyclerViewAdapter(this, photos);
        addPlaceFormPhotosRecyclerView.setAdapter(mAddPlacePhotosRecyclerViewAdapter);
        mAddPlacePhotosRecyclerViewAdapter.notifyDataSetChanged();
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
                    .position(mPlaceLatLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.heart_red)));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMarker.getPosition(), 14));
            takeSnapshotOfMap();
        }
    }

    private void takeSnapshotOfMap(){
        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap bitmap) {
                java.io.OutputStream os;
                try {
                    File file = createImageFile();
                    os = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                    mapPhoto = Uri.fromFile(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.snapshot(callback);
            }
        });

    }


    private void pickPhotoFromCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            java.io.File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.maciejak.myplaces.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
            }
        }
    }

    private java.io.File createImageFile() throws IOException {

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        java.io.File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        java.io.File image = java.io.File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        photoPaths.add(mCurrentPhotoPath);
        return image;
    }


    private void addPlaceDone() {

        mPlaceRepository = new PlaceRepository();
        mPlaceRepository.savePlace(placeTitle.getText().toString(),
                mPlaceLatLng,
                placeNote.getText().toString(),
                placeDescription.getText().toString(),
                mapPhoto,
                mPhotos);

        Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("FavouritePlaceMapActivity.ADD_PLACE_DONE");
        setResult(Activity.RESULT_OK ,intent);
        this.finish();
    }

    @Override
    public void onBackPressed() {
        trimCache();
        super.onBackPressed();
    }

    private void trimCache() {
        for(int i=0; i<mPhotos.size(); i++){
            File file = new File(photoPaths.get(i));
            file.delete();
        }
    }
}
