/**
 * @author Suraj Malviya.
 * Created on: December 19, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.smilepass.mobilesdk.exception.ClientException;
import com.smilepass.mobilesdk.exception.ServerException;
import com.smilepass.mobilesdk.listener.OnVerificationResponseListener;
import com.smilepass.mobilesdk.main.SmilePassClient;
import com.smilepass.mobilesdk.model.ServerError;
import com.smilepass.smilepasssdksample.MyApplication;
import com.smilepass.smilepasssdksample.R;
import com.smilepass.smilepasssdksample.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity implements OnVerificationResponseListener {
    private final static String TAG = VerificationActivity.class.getSimpleName();
    private EditText uniqueKeyField, selfieImageUrlField;
    private Button verifyButton;
    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);
        initView();
    }

    private void initView() {
        uniqueKeyField = findViewById(R.id.verify_uniquekey_field);
        selfieImageUrlField = findViewById(R.id.selfie_image_url);
        verifyButton = findViewById(R.id.verifyButton);
        loadingBar = findViewById(R.id.verifyUser_indeterminateBar);

        loadingBar.setVisibility(View.GONE);
    }

    public void verifyUserClick(View view) {
        SmilePassClient smilePassClient = ((MyApplication) getApplication()).getSmilePassClient();
        if (smilePassClient == null) {
            DialogUtils.openDialogToShowMessage(this, getString(R.string.smilepass_client_not_initialized));
            return;
        }
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "verifyUserClick(): Input JSON=" + registrationData.toString());
            smilePassClient.verify(registrationData, this);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private JSONObject getRegistrationJson() {
        String uniqueKey = uniqueKeyField.getText().toString();
        String selfieUrl = selfieImageUrlField.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("uniqueKey", uniqueKey);
            jsonObject.put("imageUrl", selfieUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public void onVerificationResponse(JSONObject jsonObject, Throwable throwable) {
        Log.d(TAG, "onVerificationResponse(): jsonObject=" + jsonObject + ", throwable=" + throwable);
        setFieldsEnabled(true);
        loadingBar.setVisibility(View.GONE);
        if (throwable != null) {
            if (throwable instanceof ServerException) {
                ServerError serverError = ((ServerException) throwable).error;
                if (serverError.getErrorMessage() != null)
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.server_error), serverError.getErrorMessage());
                else
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.server_error));
            } else {
                DialogUtils.openDialogToShowMessage(this, getString(R.string.unexpected_error));
            }
        } else {
            try {
                boolean status = jsonObject.getBoolean("status");
                if (status) {
                    float confidenceScore = (float) jsonObject.getDouble("confidenceScore");
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.verified_successfully), getString(R.string.verification_success_with_confidence_x, String.format("%.2f", confidenceScore)));
                } else {
                    String message = jsonObject.getString("message");
                    DialogUtils.openDialogToShowMessage(this, getString(R.string.server_error), message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setFieldsEnabled(boolean enabled) {
        this.uniqueKeyField.setEnabled(enabled);
        this.selfieImageUrlField.setEnabled(enabled);
        this.verifyButton.setEnabled(enabled);
    }
}
