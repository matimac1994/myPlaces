package com.maciejak.myplaces.ui.activities;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.LoginResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.LoginManager;
import com.maciejak.myplaces.ui.dialogs.ErrorDialog;
import com.maciejak.myplaces.utils.UserPreferencesUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginActivity extends AppCompatActivity implements
        LoginManager.LoginResponseListener,
        ServerErrorResponseListener {

    public static final int IS_REGISTER_SUCCESS_REQUEST = 1;

    @BindView(R.id.login_username)
    EditText mUserNameEditText;


    @BindView(R.id.login_password)
    EditText mPasswordEditText;

    LoginManager mLoginManager;

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
        mLoginManager = new LoginManager(this, this);
    }


    @OnClick(R.id.login_login_button)
    public void onClickLoginButton() {
//        mUsername = mUserNameEditText.getText().toString();
//        mPassword = mPasswordEditText.getText().toString();
//        mLoginManager.login(mUsername, mPassword);
        UserPreferencesUtil.setRemoteUsage("acsa");
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.login_register_button)
    public void onClickRegisterButton() {
        Intent intent = new Intent(this, RegistrationActivity.class);
        startActivityForResult(intent, IS_REGISTER_SUCCESS_REQUEST);
    }


    @OnClick(R.id.login_locally_use_text)
    public void onClickLocallyUse(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setPositiveButton(R.string.understand, (dialog, id) -> {
            UserPreferencesUtil.setLocallyUsage();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IS_REGISTER_SUCCESS_REQUEST){
            if (resultCode == RESULT_OK){
                String username = (String)data.getStringExtra(RegistrationActivity.REGISTER_USERNAME);
                mUserNameEditText.setText(username);
                mPasswordEditText.requestFocus();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSuccessResponse(LoginResponse loginResponse) {
//        UserPreferencesUtil.setRemoteUsage(loginResponse.getToken());
//        Intent intent = new Intent(this, MainActivity.class);
//        startActivity(intent);
//        finish();
    }

    @Override
    public void onErrorResponse(ErrorResponse response) {
        ErrorDialog errorDialog;
        if (response.getErrors() != null) {
            errorDialog = new ErrorDialog(this, response.getErrors().get(0).getDefaultMessage());
        }else {
            errorDialog = new ErrorDialog(this, response.getMessage());
        }
        errorDialog.show();
    }

    @Override
    public void onFailure(String message) {
        ErrorDialog errorDialog = new ErrorDialog(this, message);
        errorDialog.show();
    }

}
