package com.maciejak.myplaces.ui.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.maciejak.myplaces.R;
import com.maciejak.myplaces.api.dto.response.RegistrationResponse;
import com.maciejak.myplaces.api.dto.response.error.ErrorResponse;
import com.maciejak.myplaces.listeners.ServerErrorResponseListener;
import com.maciejak.myplaces.managers.RegistrationManager;
import com.maciejak.myplaces.ui.dialogs.ErrorDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationActivity extends AppCompatActivity implements
        RegistrationManager.RegistrationResponseListener,
        ServerErrorResponseListener {

    public static final String REGISTER_USERNAME = "RegistrationActivity USERNAME";

    @BindView(R.id.register_username)
    EditText mUserNameEditText;

    @BindView(R.id.register_email)
    EditText mEmailEditText;

    @BindView(R.id.register_password)
    EditText mPasswordEditText;

    @BindView(R.id.register_confirm_password)
    EditText mConfirmPasswordEditText;

    String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.register_register_button)
    public void onClickRegisterButton(){
        username = mUserNameEditText.getText().toString();
        RegistrationManager registrationManager = new RegistrationManager(this, this);
        registrationManager.register(mUserNameEditText.getText().toString(),
                mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString(),
                mConfirmPasswordEditText.getText().toString());
    }

    @Override
    public void onSuccessResponse(RegistrationResponse registrationResponse) {
        Toast.makeText(this, getString(R.string.registered), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.putExtra(REGISTER_USERNAME, username);
        setResult(Activity.RESULT_OK ,intent);
        finish();
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
