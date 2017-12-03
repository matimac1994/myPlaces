package com.maciejak.myplaces.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.model.Place;
import com.maciejak.myplaces.repositories.PlaceRepository;
import com.maciejak.myplaces.ui.adapters.ShowPlaceViewPagerAdapter;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowPlaceActivity extends BaseActivity {

    public static final String PLACE_ID = "ShowPlaceActivity Place Id";

    @BindView(R.id.show_place_description_content)
    TextView mDescriptionTextView;

    @BindView(R.id.show_place_note_content)
    TextView mNoteTextView;

    @BindView(R.id.show_place_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.show_place_image_view)
    ImageView mImageView;

    @BindView(R.id.show_place_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.show_place_edit_fab)
    FloatingActionButton mEditFab;

    @BindView(R.id.show_place_delete_fab)
    FloatingActionButton mDeleteFab;

    ShowPlaceViewPagerAdapter mShowPlaceViewPagerAdapter;

    long placeId;
    Place mPlace;
    PlaceRepository mPlaceRepository;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);
        ButterKnife.bind(this);
        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();
        placeId = this.getIntent().getLongExtra(PLACE_ID, 0);
        mPlaceRepository = new PlaceRepository();
    }

    private void fillControls(){
        if (mPlace.getTitle().isEmpty()){
            mCollapsingToolbarLayout.setTitle(getString(R.string.details_of_place));
        }
        else {
            mCollapsingToolbarLayout.setTitle(mPlace.getTitle());
        }

        mNoteTextView.setText(mPlace.getNote());
        mDescriptionTextView.setText(mPlace.getDescription());

        if (mPlace.getPhotos().size() == 0){
            mImageView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);

            Picasso.with(this)
                    .load(Uri.parse(mPlace.getMapPhoto()))
                    .centerCrop()
                    .fit()
                    .into(mImageView);
        }
        else {
            mImageView.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);

            mShowPlaceViewPagerAdapter = new ShowPlaceViewPagerAdapter(this, mPlace.getPhotos());
            mViewPager.setAdapter(mShowPlaceViewPagerAdapter);
        }

        if (mPlace.getDeletedAt() != null){
            mEditFab.setImageResource(R.drawable.ic_undo_white_24dp);
            mDeleteFab.setImageResource(R.drawable.ic_delete_white_24dp);
        }

    }

    @OnClick(R.id.show_place_edit_fab)
    public void editOnClick(){
        if (mPlace.getDeletedAt() != null){
            mPlaceRepository.restorePlace(mPlace);
            Toast.makeText(this, R.string.restored, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            Intent intent = new Intent(this, EditPlaceActivity.class);
            intent.putExtra(EditPlaceActivity.PLACE_ID, mPlace.getId());
            startActivity(intent);
        }


    }

    @OnClick(R.id.show_place_delete_fab)
    public void deleteOnClick(){
        if (mPlace.getDeletedAt() != null){
            mPlaceRepository.deletePlace(mPlace);
            Toast.makeText(this, R.string.deleted, Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getString(R.string.are_you_sure_to_archive));

            builder.setPositiveButton(getString(R.string.archive), (dialog, which) -> {

                mPlaceRepository.deletePlaceSoft(mPlace);
                Toast.makeText(getApplicationContext(), getText(R.string.archived), Toast.LENGTH_SHORT).show();
                finish();

            }).setNegativeButton(getString(R.string.back), (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.Red));
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.Green));
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        mPlace = mPlaceRepository.getPlaceById(placeId);
        if (mPlace != null)
            fillControls();
        super.onStart();
    }
}
