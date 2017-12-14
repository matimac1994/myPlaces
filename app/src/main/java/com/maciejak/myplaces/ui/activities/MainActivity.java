package com.maciejak.myplaces.ui.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.maciejak.myplaces.BuildConfig;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.listeners.OnCloseFloatingActionMenu;
import com.maciejak.myplaces.ui.fragments.ArchiveListFragment;
import com.maciejak.myplaces.ui.fragments.MapFragment;
import com.maciejak.myplaces.ui.fragments.MyPlacesListFragment;
import com.maciejak.myplaces.ui.fragments.SearchPlacesFragment;
import com.maciejak.myplaces.ui.fragments.TopPlacesFragment;
import com.maciejak.myplaces.utils.Const;
import com.maciejak.myplaces.utils.LogoutHandler;
import com.maciejak.myplaces.utils.PermissionUtils;
import com.maciejak.myplaces.utils.UserPreferencesUtil;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlaceSelectionListener,
        SearchPlacesFragment.OnGetInstanceFragment,
        OnCloseFloatingActionMenu{

    protected static final String TAG = MainActivity.class.getSimpleName();

    private static final int MY_LOCATION_PERMISSION_REQUEST_CODE = 11;
    private static final int REQUEST_SELECT_PLACE = 1000;
    private static final int ADD_PLACE_DONE = 2;

    private final String VISIBLE_FRAGMENT = "MainActivity Visible Fragment";

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

    Fragment mFragment;
    Fragment mLastSelectedFragment;
    FragmentManager mFragmentManager;
    FragmentTransaction mFragmentTransaction;
    int mCurrentPosition;

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

        switch (id) {
            case R.id.nav_list_places:
                mFragment = MyPlacesListFragment.newInstance();
                mCurrentPosition = R.id.nav_list_places;
                break;
            case R.id.nav_map:
                mFragment = MapFragment.newInstance();
                mCurrentPosition = R.id.nav_map;
                break;
            case R.id.nav_archive:
                mFragment = ArchiveListFragment.newInstance();
                mCurrentPosition = R.id.nav_archive;
                break;
            case R.id.nav_top_places:
                mFragment = TopPlacesFragment.newInstance();
                mCurrentPosition = R.id.nav_top_places;
                break;
            case R.id.nav_logout:
                LogoutHandler.logout(this, getString(R.string.logout_complete));
                break;
            case R.id.nav_login:
                LogoutHandler.logout(this, getString(R.string.login_or_register));
                break;
        }
        mNavigationView.setCheckedItem(mCurrentPosition);
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setupControls() {
        super.setupToolbar();
        ActionBarDrawerToggle actionBarDrawerToggle = this.setupDrawerToggle();
        mDrawerLayout.setDrawerListener(actionBarDrawerToggle);

        actionBarDrawerToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.addOnBackStackChangedListener(() -> {
            Fragment fragment = mFragmentManager.findFragmentByTag(VISIBLE_FRAGMENT);
            if (fragment instanceof MyPlacesListFragment)
                mCurrentPosition = R.id.nav_list_places;
            if (fragment instanceof MapFragment)
                mCurrentPosition = R.id.nav_map;
            if (fragment instanceof ArchiveListFragment)
                mCurrentPosition = R.id.nav_archive;
            if (fragment instanceof TopPlacesFragment)
                mCurrentPosition = R.id.nav_top_places;

            mNavigationView.setCheckedItem(mCurrentPosition);
        });

        switch (UserPreferencesUtil.checkUsageType()){
            case REMOTE:
                mNavigationView.getMenu().clear();
                mNavigationView.inflateMenu(R.menu.activity_main_drawer_remote);
                break;
            case LOCAL:
                mNavigationView.getMenu().clear();
                mNavigationView.inflateMenu(R.menu.activity_main_drawer_local);
                break;
        }

        showDefaultFragment();
        setupSettingsButton();
        setupFloatingActionMenu();
        setupLocation();
        setupSearchView();
    }

    private void replaceCurrentFragment(Fragment fragment){
        if (mLastSelectedFragment != fragment){
            mFragmentManager.popBackStack();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction.replace(R.id.contentFrameLayout, fragment, VISIBLE_FRAGMENT);
            mFragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            if (!(fragment instanceof MyPlacesListFragment))
                mFragmentTransaction.addToBackStack(null);
            mFragmentTransaction.commit();
            mLastSelectedFragment = fragment;
        }
    }

    private void showDefaultFragment() {
        mCurrentPosition = R.id.nav_list_places;
        mNavigationView.setCheckedItem(mCurrentPosition);
        mFragment = MyPlacesListFragment.newInstance();
        replaceCurrentFragment(mFragment);
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
            if (checkLocationPermissions()){
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
            }
            else {
                PermissionUtils.requestPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        MY_LOCATION_PERMISSION_REQUEST_CODE,
                        R.string.location_permission_rationale);
            }

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
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                replaceCurrentFragment(mFragment);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                closeFloatingActionMenu();
            }
        };
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
        int count = mFragmentManager.getBackStackEntryCount();

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (mFloatingActionMenu.isOpened()){
            mFloatingActionMenu.close(true);
        } else if (mMaterialSearchView.isSearchOpen()){
            mMaterialSearchView.closeSearch();
        } else if (count == 0) {
            super.onBackPressed();
        } else {
            mFragmentManager.popBackStack();
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
        if (checkLocationPermissions()) {
            startLocationUpdates(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
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
                startLocationUpdates(this);
            } else {
                PermissionUtils.showSnackbar(this, R.string.permission_denied_explanation,
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

    @Override
    public void closeFloatingActionMenu() {
        if (mFloatingActionMenu.isOpened()){
            mFloatingActionMenu.close(true);
        }
    }
}
