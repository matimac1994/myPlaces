package com.maciejak.myplaces.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.maciejak.myplaces.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener {

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private static final int REQUEST_SELECT_PLACE = 1000;
    public static final String SELECTED_FAVOURITE_PLACE_NAME = "MainActivity SELECTED_FAVOURITE_PLACE_NAME";
    public static final String SELECTED_FAVOURITE_PLACE_LATLNG = "MainActivity SELECTED_FAVOURITE_PLACE_LATLNG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupControls();
    }

    private void setupControls(){
        super.setupToolbar();

        mActionBarDrawerToggle = this.setupDrawerToggle();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mActionBarDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        showDefaultFragment();
        setupSettingsButton();

    }

    private void showDefaultFragment(){
        mNavigationView.setCheckedItem(R.id.nav_list_places);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = MyPlacesListFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.contentFrameLayout, fragment)
                .commit();
    }

    private void setupSettingsButton(){
        ImageButton settingsButton = (ImageButton) mNavigationView.getHeaderView(0).findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO change to settings activity
                Intent intent = new Intent(getApplicationContext(), SplashScreenActivity.class);
                startActivity(intent);
            }
        });
    }

    private ActionBarDrawerToggle setupDrawerToggle(){
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            // Method #3
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(MainActivity.this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();

        switch (id){
            case R.id.nav_about:
                Toast.makeText(this, "O aplikacji", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
            case R.id.nav_list_places:
                fragment = MyPlacesListFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment)
                        .commit();
                break;
            case R.id.nav_map:
                fragment = MapFragment.newInstance();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment)
                        .commit();
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onPlaceSelected(Place place) {
        Intent intent = new Intent(this, FavouritePlaceMapActivity.class);
        intent.putExtra(SELECTED_FAVOURITE_PLACE_NAME, place.getName());
        intent.putExtra(SELECTED_FAVOURITE_PLACE_LATLNG, place.getLatLng());
        startActivity(intent);
    }

    @Override
    public void onError(Status status) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
