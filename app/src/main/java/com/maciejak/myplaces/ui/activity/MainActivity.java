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
        SearchPlacesFragment.OnGetInstanceFragment{
    protected static final String TAG = BaseActivity.class.getSimpleName();

    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int ADD_PLACE_DONE = 2;

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 11;
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;
    private static final int REQUEST_CHECK_SETTINGS = 0x1;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationRequest mLocationRequest;
    private SettingsClient mSettingsClient;
    private LocationCallback mLocationCallback;
    private Location mLocation;
    private Boolean isActualLocation;

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

        mDrawerLayout.closeDrawer(GravityCompat.START);
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

    private void setupLocation() {
        isActualLocation = false;
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSettingsClient = LocationServices.getSettingsClient(this);

        createLocationCallback();
        createLocationRequest();
        buildLocationSettingsRequest();

    }

    private void createLocationCallback() {
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                if (locationResult != null) {
                    isActualLocation = true;
                    mLocation = locationResult.getLastLocation();
                    mSharedPreferences.edit().putString(Const.LATITUDE, Double.toString(mLocation.getLatitude())).apply();
                    mSharedPreferences.edit().putString(Const.LONGITUDE, Double.toString(mLocation.getLongitude())).apply();
                }
            }
        };
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();
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
            mFloatingActionMenu.close(true);
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
            mFloatingActionMenu.close(true);
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
                mFloatingActionMenu.close(true);
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
        } else if (requestCode == REQUEST_CHECK_SETTINGS) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    Log.i(TAG, "User agreed to make required location settings changed");
                    break;
                case RESULT_CANCELED:
                    Log.i(TAG, "User don't agreed to make required location settings changed");
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (checkPermissions()) {
            startLocationUpdates();
        } else {
            requestPermissions();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActualLocation = false;
        stopLocationUpdates();
    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdates() {
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this, locationSettingsResponse -> {
                    mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                })
                .addOnFailureListener(this, (e) ->{
                    int statusCode = ((ApiException) e).getStatusCode();
                    switch (statusCode) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                    "location settings ");
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                ResolvableApiException rae = (ResolvableApiException) e;
                                rae.startResolutionForResult(MainActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException sie) {
                                Log.i(TAG, "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Toast.makeText(MainActivity.this, R.string.location_request_settings_is_inadequate, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void stopLocationUpdates() {
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
    }

    //true if permissions granted, false otherwise
    private boolean checkPermissions() {
        int permissionFineLocation = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCoarseLocation = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return (permissionFineLocation == PackageManager.PERMISSION_GRANTED
                && permissionCoarseLocation == PackageManager.PERMISSION_GRANTED);
    }

    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_FINE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    MY_LOCATION_PERMISSION_REQUEST_CODE);
                        }
                    });
        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == MY_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permission granted, updates requested, starting location updates");
                startLocationUpdates();
            } else {
                showSnackbar(R.string.permission_denied_explanation,
                        R.string.settings, view -> {
                            // Build intent that displays the App settings screen.
                            Intent intent = new Intent();
                            intent.setAction(
                                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package",
                                    BuildConfig.APPLICATION_ID, null);
                            intent.setData(uri);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        });
            }
        }
    }

    @Override
    public void getFragmentInstance(Fragment fragment) {
        mSearchPlaceFragment = (SearchPlacesFragment) fragment;
    }
}
