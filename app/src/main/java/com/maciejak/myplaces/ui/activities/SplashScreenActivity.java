package com.maciejak.myplaces.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maciejak.myplaces.utils.UserPreferencesUtil;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        switch (UserPreferencesUtil.checkUsageType()){
            case LOCAL:
            case REMOTE:
                intent = new Intent(this, MainActivity.class);
                break;
            case NONE:
            default:
                intent = new Intent(this, LoginActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
}
