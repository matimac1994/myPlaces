package com.maciejak.myplaces.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maciejak.myplaces.R;

public class ShowPlaceActivity extends AppCompatActivity {

    public static final String PLACE_ID = "ShowPlaceActivity Place Id";

    private long placeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_place);

        placeId = this.getIntent().getLongExtra(PLACE_ID, 0);


    }
}
