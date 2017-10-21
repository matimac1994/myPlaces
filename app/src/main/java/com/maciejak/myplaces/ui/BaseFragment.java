package com.maciejak.myplaces.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.View;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.maciejak.myplaces.R;


/**
 * Created by Mati on 21.10.2017.
 */

public class BaseFragment extends Fragment {

    FloatingActionButton mAddPlaceFromMyLocationActionButton;
    FloatingActionButton mAddPlaceFromMapActionButton;

    protected void configFloatingActionMenu(Context context, FloatingActionMenu floatingActionMenu){

        mAddPlaceFromMyLocationActionButton = configFromMyLocationActionButton(context);
        mAddPlaceFromMapActionButton = configFromMapActionButton(context);

        floatingActionMenu.addMenuButton(mAddPlaceFromMyLocationActionButton);
        floatingActionMenu.addMenuButton(mAddPlaceFromMapActionButton);

        floatingActionMenu.setClosedOnTouchOutside(true);

    }

    protected FloatingActionButton configFromMyLocationActionButton(Context context){
        FloatingActionButton fabButton = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_form_my_location_button_label));
        fabButton.setImageResource(R.drawable.ic_my_location_white_24dp);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return fabButton;
    }

    protected FloatingActionButton configFromMapActionButton(final Context context){
        FloatingActionButton fabButton  = new FloatingActionButton(context);
        fabButton.setButtonSize(FloatingActionButton.SIZE_MINI);
        fabButton.setLabelText(getString(R.string.add_place_form_map_button_label));
        fabButton.setImageResource(R.drawable.ic_add_location_white_24dp);
        fabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FavouritePlaceMapActivity.class);
                startActivity(intent);
            }
        });
        return fabButton;
    }

}