package com.maciejak.myplaces.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.maciejak.myplaces.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity {

    public static final int IS_REGISTER_SUCCESS_REQUEST = 1;

    private String mUsername;
    private String mPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupControls();
    }

    private void setupControls() {
    }


    @OnClick(R.id.login_login_button)
    public void onClickLoginButton() {
        doLogin(mUsername, mPassword);
    }

    @OnClick(R.id.login_register_button)
    public void onClickRegisterButton() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
//        startActivityForResult(intent, IS_REGISTER_SUCCESS_REQUEST);
    }


    @OnClick(R.id.login_locally_use_text)
    public void onClickLocallyUse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.understand, (dialog, id) -> {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
        });

        builder.setNegativeButton(R.string.cancel, ((dialog, which) -> dialog.dismiss()));

        builder.setTitle(R.string.login_locally_use_alert_title);
        builder.setIcon(R.drawable.ic_info_outline_black_24dp);
        builder.setMessage(R.string.login_locally_use_alert_message);

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void doLogin(String username, String password){

    }

}
