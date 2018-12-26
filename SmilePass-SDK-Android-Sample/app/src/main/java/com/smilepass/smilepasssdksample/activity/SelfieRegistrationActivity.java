/**
 * @author Suraj Malviya.
 * Created on: December 19, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.smilepass.mobilesdk.exception.ClientException;
import com.smilepass.mobilesdk.exception.ServerException;
import com.smilepass.mobilesdk.listener.OnRegistrationResponseListener;
import com.smilepass.mobilesdk.main.SmilePassClient;
import com.smilepass.mobilesdk.model.ServerError;
import com.smilepass.smilepasssdksample.MyApplication;
import com.smilepass.smilepasssdksample.R;
import com.smilepass.smilepasssdksample.utils.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelfieRegistrationActivity extends AppCompatActivity implements OnRegistrationResponseListener, DialogInterface.OnClickListener {
    private final static String TAG = SelfieRegistrationActivity.class.getSimpleName();
    private EditText uniqueKeyField, callbackUrlField, selfieImageUrlField1, selfieImageUrlField2;
    private Button registerButton;
    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_register);
        initView();
        handleInputIntent();
    }

    private void initView() {
        this.uniqueKeyField = findViewById(R.id.selfie_uniquekey_field);
        this.selfieImageUrlField1 = findViewById(R.id.selfie_url_1_text);
        this.selfieImageUrlField2 = findViewById(R.id.selfie_url_2_text);
        this.callbackUrlField = findViewById(R.id.callbackUrl_field);
        this.registerButton = findViewById(R.id.register_button);
        this.loadingBar = findViewById(R.id.indeterminateBar);

        this.loadingBar.setVisibility(View.GONE);
    }

    private void handleInputIntent() {
        String uniqueKey = "";
        if (getIntent().hasExtra("uniqueKey"))
            uniqueKey = getIntent().getExtras().getString("uniqueKey");

        this.uniqueKeyField.setText(uniqueKey);
    }

    @Override
    public void onRegistrationResponse(JSONObject jsonObject, Throwable throwable) {
        setFieldsEnabled(true);
        loadingBar.setVisibility(View.GONE);

        if (throwable != null) {
            if (throwable instanceof ServerException) {
                ServerError serverError = ((ServerException) throwable).error;
                Log.e(TAG, serverError.getErrorMessage());
                if (serverError.getErrorMessage() != null) {
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.server_error), serverError.getErrorMessage());
                } else {
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.server_error));
                }
            } else {
                DialogUtils.openDialogToShowMessage(this, getString(R.string.unexpected_error));
            }
        } else {
            DialogUtils.openDialogWithOkBtn(this, getString(R.string.registration_success), getString(R.string.selfie_registration_success), this);
        }
    }

    public void registerWithSelfie(View view) {
        SmilePassClient smilePassClient = ((MyApplication) getApplication()).getSmilePassClient();
        if (smilePassClient == null) {
            DialogUtils.openDialogToShowMessage(this, getString(R.string.smilepass_client_not_initialized));
            return;
        }
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "JSON Going for selfie registration: " + registrationData.toString());
            smilePassClient.register(registrationData, this);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getRegistrationJson() {
        String uniqueKey = this.uniqueKeyField.getText().toString();
        String callbackURL = this.callbackUrlField.getText().toString();
        String selfieImage1 = this.selfieImageUrlField1.getText().toString();
        String selfieImage2 = this.selfieImageUrlField2.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("step", "selfie");
            jsonObject.put("uniqueKey", uniqueKey);
            jsonObject.put("callbackUrl", callbackURL); //optional
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(selfieImage1);
            jsonArray.put(selfieImage2);
            jsonObject.put("imageUrls", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void setFieldsEnabled(boolean enabled) {
        this.uniqueKeyField.setEnabled(enabled);
        this.selfieImageUrlField2.setEnabled(enabled);
        this.selfieImageUrlField1.setEnabled(enabled);
        this.callbackUrlField.setEnabled(enabled);
        this.registerButton.setEnabled(enabled);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
