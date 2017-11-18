package com.maciejak.myplaces.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.BuildConfig;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.listener.OnCloseFloatingActionMenu;
import com.maciejak.myplaces.ui.fragment.ArchiveListFragment;
import com.maciejak.myplaces.ui.fragment.MapFragment;
import com.maciejak.myplaces.ui.fragment.MyPlacesListFragment;
import com.maciejak.myplaces.ui.fragment.SearchPlacesFragment;
import com.maciejak.myplaces.util.Const;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener,
        SearchPlacesFragment.OnGetInstanceFragment,
        OnCloseFloatingActionMenu{

    protected static final String TAG = MainActivity.class.getSimpleName();

    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int ADD_PLACE_DONE = 2;

    private Toast mToast;

    FloatingActionButton mAddPlaceFromMyLocationActionButton;
    FloatingActionButton mAddPlaceFromMapActionButton;
    FloatingActionButton mAddPlaceFromSearchActionButton;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_place_menu)
    FloatingActionMenu mFloatingActionMenu;
    @BindView(R.id.material_search_view)
    MaterialSearchView mMaterialSearchView;

    SearchPlacesFragment mSearchPlaceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupControls();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment;
        FragmentManager fragmentManager = getSupportFragmentManager();
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch (id) {
            case R.id.nav_list_places:
                fragment = MyPlacesListFragment.newInstance();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment)
                        .commit();
                break;
            case R.id.nav_map:
                fragment = MapFragment.newInstance();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment)
                        .addToBackStack("MapFragment")
                        .commit();
                break;
            case R.id.nav_archive:
                fragment = ArchiveListFragment.newInstance();
                fragmentManager.popBackStack();
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment)
                        .addToBackStack("ArchiveListFragment")
                        .commit();
                break;
            case R.id.nav_about:
                Toast.makeText(this, "O aplikacji", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_logout:
                Toast.makeText(this, "Wylogowano", Toast.LENGTH_SHORT).show();
                this.finish();
                break;
        }
        return true;
    }

    private void setupControls() {
        super.setupToolbar();
        ActionBarDrawerToggle actionBarDrawerToggle = this.setupDrawerToggle();
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        showDefaultFragment();
        setupSettingsButton();
        setupFloatingActionMenu();
        setupLocation();
        setupSearchView();
    }

    private void setupSearchView() {
        final Fragment fragment = SearchPlacesFragment.newInstance();
        final FragmentManager fragmentManager = getSupportFragmentManager();

        mMaterialSearchView.setVoiceSearch(true);

        mMaterialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                fragmentManager.beginTransaction()
                        .replace(R.id.contentFrameLayout, fragment, "SearchPlacesFragment")
                        .addToBackStack("SearchPlacesFragment")
                        .commit();
                mDrawerLayout.closeDrawer(GravityCompat.START);
                mFloatingActionMenu.hideMenu(true);
            }

            @Override
            public void onSearchViewClosed() {
                fragmentManager.popBackStack("SearchPlacesFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                mFloatingActionMenu.showMenu(true);
            }
        });

        mMaterialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Hide keyboard
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (mSearchPlaceFragment != null)
                    mSearchPlaceFragment.onQueryTextChange(newText);
                return true;
            }
        });
    }

    private void showDefaultFragment() {
        mNavigationView.setCheckedItem(R.id.nav_list_places);
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = MyPlacesListFragment.newInstance();
        fragmentManager.beginTransaction()
                .replace(R.id.contentFrameLayout, fragment)
                .commit();
    }

    private void setupSettingsButton() {
        ImageButton settingsButton = (ImageButton) mNavigationView.getHeaderView(0).findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
        });
    }

    private void setupFloatingActionMenu() {
        mAddPlaceFromMyLocationActionButton = configFromMyLocationActionButton(this);
        mAddPlaceFromMapActionButton = configFromMapActionButton(this);
        mAddPlaceFromSearchActionButton = configFromSearchActionButton(this);

        mFloatingActionMenu.addMenuButton(mAddPlaceFromSearchActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMapActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMyLocationActionButton);

        mFloatingActionMenu.setClosedOnTouchOutside(true);

    }

    private FloatingActionButton configFromMyLocationActionButton(Context context) {
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_my_location_button_label));
        fabButton.setImageResource(R.drawable.ic_my_location_white_24dp);
        fabButton.setOnClickListener(v -> {
            Intent intent;
            if (isActualLocation) {
                intent = new Intent(context, AddPlaceActivity.class);
                intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, new LatLng(mLocation.getLatitude(), mLocation.getLongitude()));
            } else {
                if (mSharedPreferences.contains(Const.LONGITUDE) && mSharedPreferences.contains(Const.LATITUDE)) {
                    mToast = Toast.makeText(context, R.string.last_known_location, Toast.LENGTH_SHORT);
                    intent = new Intent(context, AddPlaceOnMapActivity.class);
                    Double latitude = Double.parseDouble(mSharedPreferences.getString(Const.LATITUDE, "0"));
                    Double longitude = Double.parseDouble(mSharedPreferences.getString(Const.LONGITUDE, "0"));
                    intent.putExtra(AddPlaceOnMapActivity.SELECTED_PLACE_LATLNG, new LatLng(latitude, longitude));
                } else {
                    mToast = Toast.makeText(context, R.string.cannot_find_location_alert, Toast.LENGTH_SHORT);
                    intent = new Intent(context, AddPlaceOnMapActivity.class);
                }
                mToast.show();
            }
            startActivityForResult(intent, ADD_PLACE_DONE);
        });
        return fabButton;
    }

    private FloatingActionButton configFromMapActionButton(final Context context) {
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_map_button_label));
        fabButton.setImageResource(R.drawable.ic_add_location_white_24dp);
        fabButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddPlaceOnMapActivity.class);
            startActivityForResult(intent, ADD_PLACE_DONE);
        });
        return fabButton;
    }

    private FloatingActionButton configFromSearchActionButton(Context context) {
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_search_button_label));
        fabButton.setImageResource(R.drawable.ic_search_white_24dp);
        fabButton.setOnClickListener(v -> {
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(this);
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
        return fabButton;
    }

    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        mMaterialSearchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mFloatingActionMenu.isOpened()){
            mFloatingActionMenu.close(true);
        } else if (mMaterialSearchView.isSearchOpen()){
            mMaterialSearchView.closeSearch();
        } else if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onPlaceSelected(Place place) {
        Intent intent = new Intent(this, AddPlaceOnMapActivity.class);
        intent.putExtra(AddPlaceOnMapActivity.SELECTED_PLACE_LATLNG, place.getLatLng());
        startActivityForResult(intent, ADD_PLACE_DONE);
    }

    @Override
    public void onError(Status status) {}

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                this.onError(status);
            }
        } else if (requestCode == ADD_PLACE_DONE) {
            if (resultCode == RESULT_OK) {
//                actionAfterAddPlaceDone(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissions()) {
            startLocationUpdates(this);
        } else {
            requestPermissions(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void getFragmentInstance(Fragment fragment) {
        mSearchPlaceFragment = (SearchPlacesFragment) fragment;
    }

    @Override
    public void closeFloatingActionMenu() {
        if (mFloatingActionMenu.isOpened()){
            mFloatingActionMenu.close(true);
        }
    }
}
