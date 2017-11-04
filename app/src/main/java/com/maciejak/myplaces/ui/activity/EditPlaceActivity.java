package com.maciejak.myplaces.ui.activity;

import android.os.Bundle;

import com.maciejak.myplaces.R;

public class EditPlaceActivity extends BaseActivity {

    public static final String PLACE_ID = "EditPlaceActivity PLACE_ID";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_place);

        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();
        this.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    }
}
