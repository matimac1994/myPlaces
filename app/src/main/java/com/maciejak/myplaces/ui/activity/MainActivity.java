package com.maciejak.myplaces.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.helper.GoogleApiClientHelper;
import com.maciejak.myplaces.ui.fragment.MapFragment;
import com.maciejak.myplaces.ui.fragment.MyPlacesListFragment;
import com.maciejak.myplaces.util.Const;
import com.maciejak.myplaces.util.PermissionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener, GoogleApiClientHelper.ConnectionListener{

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int ADD_PLACE_DONE = 2;

    FloatingActionButton mAddPlaceFromMyLocationActionButton;
    FloatingActionButton mAddPlaceFromMapActionButton;
    FloatingActionButton mAddPlaceFromSearchActionButton;

    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @BindView(R.id.nav_view)
    NavigationView mNavigationView;
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.add_place_menu)
    FloatingActionMenu mFloatingActionMenu;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private SharedPreferences mSharedPreferences;
    private Location mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();

        mActionBarDrawerToggle = this.setupDrawerToggle();
        mDrawerLayout.setDrawerListener(mActionBarDrawerToggle);

        mActionBarDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        showDefaultFragment();
        setupSettingsButton();
        setupLocation();
        setupFloatingActionMenu();

    }

    private void setupLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mSharedPreferences = this.getSharedPreferences(Const.LOCATION, Context.MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            mFusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                if (location != null){
                    mSharedPreferences.edit().putString(Const.LATITUDE, Double.toString(location.getLatitude())).apply();
                    mSharedPreferences.edit().putString(Const.LONGITUDE, Double.toString(location.getLongitude())).apply();
                }
            });
        }

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
            getMyLocation();
            if (mLocation != null){
                intent = new Intent(context, AddPlaceActivity.class);
                intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, new LatLng(mLocation.getLongitude(), mLocation.getLatitude()));
            }
            else {
                if (mSharedPreferences.contains(Const.LONGITUDE) && mSharedPreferences.contains(Const.LATITUDE)){
                    Toast.makeText(context, R.string.last_known_location, Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, AddPlaceActivity.class);
                    Double latitude = Double.parseDouble(mSharedPreferences.getString(Const.LATITUDE, "0"));
                    Double longitude = Double.parseDouble(mSharedPreferences.getString(Const.LONGITUDE, "0"));
                    intent.putExtra(AddPlaceActivity.PLACE_LAT_LNG, new LatLng(latitude, longitude));
                }
                else {
                    Toast.makeText(context, R.string.cannot_find_location_alert, Toast.LENGTH_SHORT).show();
                    intent = new Intent(context, AddPlaceOnMapActivity.class);
                }
            }
            mFloatingActionMenu.close(true);
            startActivityForResult(intent, ADD_PLACE_DONE);
        });
        return fabButton;
    }

    private FloatingActionButton configFromMapActionButton(final Context context){
        FloatingActionButton fabButton  = new FloatingActionButton(context);
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

    private FloatingActionButton configFromSearchActionButton(Context context){
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

    private ActionBarDrawerToggle setupDrawerToggle(){
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if(count == 0){
            super.onBackPressed();
        }
        else{
            getSupportFragmentManager().popBackStack();
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
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnected(Bundle bundle) {

        mLocation = getMyLocation();
    }


    @Override
    public void onPlaceSelected(Place place) {
        Intent intent = new Intent(this, AddPlaceOnMapActivity.class);
        intent.putExtra(AddPlaceOnMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG, place.getLatLng());
        startActivityForResult(intent, ADD_PLACE_DONE);
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
        else if (requestCode == ADD_PLACE_DONE){
            if (resultCode == RESULT_OK){
                actionAfterAddPlaceDone(data);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void actionAfterAddPlaceDone(Intent data) {
    }

    public void requestLocationPermission(int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Display a dialog with rationale.
            PermissionUtils.RationaleDialog
                    .newInstance(requestCode, false).show(getSupportFragmentManager(),"dialog");
        } else {
            // Location permission has not been granted yet, request it.
            PermissionUtils.requestPermission(this, requestCode,
                    Manifest.permission.ACCESS_FINE_LOCATION, false);
        }
    }

    public Location getMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermission(MY_LOCATION_PERMISSION_REQUEST_CODE);
        }
        else {
            mFusedLocationProviderClient.flushLocations();
            mFusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            mLocation = location;
                        }
                    });
        }
        return mLocation;
    }
}
