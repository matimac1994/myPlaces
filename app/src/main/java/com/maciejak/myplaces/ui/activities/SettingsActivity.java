package com.maciejak.myplaces.ui.activities;

import android.os.Bundle;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.utils.LogoutHandler;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setupControls();
    }

    private void setupControls() {
        super.setupToolbar();

        getSupportActionBar().setTitle(R.string.settings);

//        LogoutHandler.logout(this, getString(R.string.logout_complete));
//        LogoutHandler.logout(this, getString(R.string.login_or_register));
    }

    @OnClick(R.id.settings_logout)
    public void logoutOnClick(){
        LogoutHandler.logout(this, getString(R.string.logout_complete));
    }
}
