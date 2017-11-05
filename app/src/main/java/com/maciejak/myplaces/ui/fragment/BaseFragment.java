package com.maciejak.myplaces.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.maciejak.myplaces.R;
import com.maciejak.myplaces.ui.activity.AddPlaceOnMapActivity;

import static android.app.Activity.RESULT_OK;
import static com.maciejak.myplaces.ui.activity.AddPlaceOnMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG;


/**
 * Created by Mati on 21.10.2017.
 */

public class BaseFragment extends Fragment implements PlaceSelectionListener {

    private static final int REQUEST_SELECT_PLACE = 1000;

    FloatingActionMenu mFloatingActionMenu;
    FloatingActionButton mAddPlaceFromMyLocationActionButton;
    FloatingActionButton mAddPlaceFromMapActionButton;
    FloatingActionButton mAddPlaceFromSearchActionButton;

    protected void configFloatingActionMenu(Context context, FloatingActionMenu floatingActionMenu){
        mFloatingActionMenu = floatingActionMenu;

        mAddPlaceFromMyLocationActionButton = configFromMyLocationActionButton(context);
        mAddPlaceFromMapActionButton = configFromMapActionButton(context);
        mAddPlaceFromSearchActionButton = configFromSearchActionButton(context);

        mFloatingActionMenu.addMenuButton(mAddPlaceFromSearchActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMapActionButton);
        mFloatingActionMenu.addMenuButton(mAddPlaceFromMyLocationActionButton);

        mFloatingActionMenu.setClosedOnTouchOutside(true);

    }

    protected FloatingActionButton configFromMyLocationActionButton(Context context){
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_my_location_button_label));
        fabButton.setImageResource(R.drawable.ic_my_location_white_24dp);
        fabButton.setOnClickListener(v -> {

        });
        return fabButton;
    }

    protected FloatingActionButton configFromMapActionButton(final Context context){
        FloatingActionButton fabButton  = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_map_button_label));
        fabButton.setImageResource(R.drawable.ic_add_location_white_24dp);
        fabButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddPlaceOnMapActivity.class);
            startActivity(intent);
            mFloatingActionMenu.close(true);
        });
        return fabButton;
    }

    protected FloatingActionButton configFromSearchActionButton(Context context){
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_from_search_button_label));
        fabButton.setImageResource(R.drawable.ic_search_white_24dp);
        fabButton.setOnClickListener(v -> {
            try {
                Intent intent = new PlaceAutocomplete.IntentBuilder
                        (PlaceAutocomplete.MODE_FULLSCREEN)
                        .build(getActivity());
                startActivityForResult(intent, REQUEST_SELECT_PLACE);
                mFloatingActionMenu.close(true);
            } catch (GooglePlayServicesRepairableException |
                    GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        });
        return fabButton;
    }


    @Override
    public void onPlaceSelected(Place place) {
        Intent intent = new Intent(getContext(), AddPlaceOnMapActivity.class);
        intent.putExtra(AddPlaceOnMapActivity.SELECTED_FAVOURITE_PLACE_LATLNG, place.getLatLng());
        startActivity(intent);
    }

    @Override
    public void onError(Status status) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SELECT_PLACE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(getContext(), data);
                this.onPlaceSelected(place);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(getContext(), data);
                this.onError(status);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
