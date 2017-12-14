package com.maciejak.myplaces.ui.activities;

import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.TopPlacePhotoResponse;
import com.maciejak.myplaces.api.dto.response.TopPlaceResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.TopPlaces.ShowTopPlaceManager;
import com.maciejak.myplaces.ui.adapters.ShowPlaceViewPagerAdapter;
import com.maciejak.myplaces.ui.adapters.ShowTopPlaceViewPagerAdapter;
import com.squareup.picasso.Picasso;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShowTopPlaceActivity extends BaseActivity implements
        ServerErrorResponseListener,
        ShowTopPlaceManager.ShowTopPlaceManagerListener{

    public static final String PLACE_ID = "ShowTopPlaceActivity TopPlaceId";

    @BindView(R.id.show_top_place_collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.show_top_place_image_view)
    ImageView mImageView;

    @BindView(R.id.show_top_place_view_pager)
    ViewPager mViewPager;

    @BindView(R.id.show_top_place_description_content)
    TextView mDescriptionTextView;


    ShowTopPlaceViewPagerAdapter mShowTopPlaceViewPagerAdapter;

    private ShowTopPlaceManager mShowTopPlaceManager;
    private TopPlaceResponse mTopPlace;
    private Long placeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_top_place);
        ButterKnife.bind(this);
        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();
        placeId = this.getIntent().getLongExtra(PLACE_ID, 0);
        mShowTopPlaceManager = new ShowTopPlaceManager(this, this, this);
    }

    private void fillControls() {
        mCollapsingToolbarLayout.setTitle(mTopPlace.getName());
        mDescriptionTextView.setText(mTopPlace.getDescription());

        if (mTopPlace.getPhotos().size() == 0){
            mImageView.setVisibility(View.VISIBLE);
            mViewPager.setVisibility(View.GONE);
            Picasso.with(this)
                    .load(Uri.parse(mTopPlace.getMainPhoto()))
                    .centerCrop()
                    .fit()
                    .into(mImageView);
        } else {
            mImageView.setVisibility(View.GONE);
            mViewPager.setVisibility(View.VISIBLE);
            mShowTopPlaceViewPagerAdapter = new ShowTopPlaceViewPagerAdapter(this, mTopPlace.getPhotos());
            mViewPager.setAdapter(mShowTopPlaceViewPagerAdapter);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mShowTopPlaceManager.getTopPlaceById(placeId);
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
    public void onErrorResponse(ErrorResponse response) {
        if (response.getErrors() != null) {
            Toast.makeText(this, response.getErrors().get(0).getDefaultMessage(), Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, response.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    @Override
    public void onFailure(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onGetTopPlace(TopPlaceResponse topPlace) {
        mTopPlace = topPlace;
        fillControls();
    }
}
