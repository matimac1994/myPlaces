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
import android.widget.ImageView;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.PlacePhotoResponse;
import com.maciejak.myplaces.api.dto.response.PlaceResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.EditPlaceManager;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.model.PlacePhoto;
import com.maciejak.myplaces.repositories.PlacePhotoRepository;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.ui.adapters.EditPlacePhotosRecyclerViewAdapter;
import com.maciejak.myplaces.utils.FileUtils;
import com.maciejak.myplaces.utils.PermissionUtils;
import com.squareup.picasso.Picasso;

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

public class EditPlaceActivity extends BaseActivity
        implements EditPlaceManager.EditPlaceResponseListener,
        ServerErrorResponseListener, EditPlacePhotosRecyclerViewAdapter.EditPlacePhotosRecyclerViewAdapterListener {

    public static final String PLACE_ID = "EditPlaceActivity PLACE_ID";
    private static final int REQUEST_CAMERA = 11;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 12;

    @BindView(R.id.edit_place_collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;

    @BindView(R.id.edit_place_image_view)
    ImageView mImageView;

    @BindView(R.id.edit_place_photos_recycler_view)
    RecyclerView mEditPlacePhotoRecyclerView;

    EditPlacePhotosRecyclerViewAdapter mEditPlacePhotoRecyclerViewAdapter;

    @BindView(R.id.edit_place_title)
    EditText placeTitle;

    @BindView(R.id.edit_place_note)
    EditText placeNote;

    @BindView(R.id.edit_place_describe)
    EditText placeDescription;

    long mPlaceId;
    PlaceResponse mPlace;
    List<PlacePhotoResponse> mPhotos;

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_TAKE_PHOTO= 2;
    private EditPlaceManager mEditPlaceManager;
    private Uri photoURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);
        ButterKnife.bind(this);

        mPlaceId = getIntent().getLongExtra(PLACE_ID, 0);

        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();
        if (this.getSupportActionBar() != null)
            this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
        collapsingToolbar.setTitle(getString(R.string.edit_place));

        mEditPlaceManager = new EditPlaceManager(this, this, this);
        mEditPlaceManager.getPlaceById(mPlaceId);
    }

    private void setUpRecyclerView(List<PlacePhotoResponse> photos){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.HORIZONTAL, false);
        mEditPlacePhotoRecyclerView.setLayoutManager(layoutManager);
        mEditPlacePhotoRecyclerViewAdapter = new EditPlacePhotosRecyclerViewAdapter(this, photos, this);
        mEditPlacePhotoRecyclerView.setAdapter(mEditPlacePhotoRecyclerViewAdapter);
        mEditPlacePhotoRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void fillControls(PlaceResponse place){
        placeTitle.setText(place.getTitle());
        placeDescription.setText(place.getDescription());
        placeNote.setText(place.getNote());
        Picasso.with(this)
                .load(place.getMapPhoto())
                .fit()
                .centerCrop()
                .into(mImageView);
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
                editPlaceDone();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode==RESULT_OK)
                {
                    File file = FileUtils.savePhotoToFile(data.getData(), this);
                    mEditPlaceManager.addPhoto(Uri.fromFile(file));
                }
                break;
            case RESULT_TAKE_PHOTO:
                if(resultCode==RESULT_OK)
                {
                    mEditPlaceManager.addPhoto(photoURI);
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void editPlaceDone() {
        mEditPlaceManager.editPlace(mPlace.getId(),
                placeTitle.getText().toString(),
                placeDescription.getText().toString(),
                placeNote.getText().toString(),
                mPhotos);
    }

    @OnClick(R.id.edit_place_add_photo_fab)
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
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    photoURI = FileUtils.createUriForTakePhoto(this);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, RESULT_TAKE_PHOTO);
                }
            }
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
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
    public void onGetPlace(PlaceResponse placeResponse) {
        mPlace = placeResponse;
        mPhotos = mPlace.getPhotos();
        setUpRecyclerView(mPhotos);
        fillControls(mPlace);
    }

    @Override
    public void onSuccessResponse() {
        Toast.makeText(this, getText(R.string.saved), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("AddPlaceOnMapActivity.ADD_PLACE_DONE");
        setResult(Activity.RESULT_OK ,intent);
        this.finish();
    }

    @Override
    public void onGetPlaceError(String string) {

    }

    @Override
    public void onUploadPhoto(PlacePhotoResponse placePhotoResponse) {
        mPhotos.add(placePhotoResponse);
        mEditPlacePhotoRecyclerViewAdapter.notifyItemInserted(mEditPlacePhotoRecyclerViewAdapter.getItemCount());
    }

    @Override
    public void onErrorResponse(ErrorResponse response) {

    }

    @Override
    public void onFailure(String message) {

    }

    @Override
    public void onClickDeletePhotoButton(PlacePhotoResponse placePhotoResponse, int position) {
        mPhotos.remove(position);
        mEditPlacePhotoRecyclerViewAdapter.notifyItemRemoved(position);
    }
}
