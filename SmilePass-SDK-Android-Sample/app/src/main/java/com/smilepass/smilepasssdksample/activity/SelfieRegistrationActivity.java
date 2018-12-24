/**
 * @author Suraj Malviya.
 * Created on: December 19, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.smilepass.smilepasssdksample.util.DialogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SelfieRegistrationActivity extends AppCompatActivity implements OnRegistrationResponseListener, DialogInterface.OnClickListener {

    private EditText uniqueKeyField, callbackUrlField, selfieImageUrlField1, selfieImageUrlField2;
    private Button registerButton;

    private ProgressBar loadingBar;

    private final String TAG = "SELFIE REGISTRATION";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selfie_register);

        String uniqueKey = "";
        if(getIntent().hasExtra("uniqueKey")) uniqueKey =  getIntent().getExtras().getString("uniqueKey");

        initResources();

        this.loadingBar.setVisibility(View.GONE);
        this.uniqueKeyField.setText(uniqueKey);

        //TODO: Comment below lines for QA/Build
        this.selfieImageUrlField1.setText("https://ga-core.s3.amazonaws.com/production/uploads/instructor/image/14769/thumb_AAEAAQAAAAAAAAH_AAAAJDE0M2I3OWMzLTM1MGItNDE2OC04MjNkLWYxZmNiMTQyNmU1Zg.jpg");
        this.selfieImageUrlField2.setText("https://i1.wp.com/smile-pass.com/wp-content/uploads/2018/07/Cto.jpg?resize=540%2C429&ssl=1");
        this.callbackUrlField.setText("https://webhook.site/091b56f3-0058-4134-95f8-20458d5306af");

    }

    private void initResources() {
        this.uniqueKeyField = findViewById(R.id.selfie_uniquekey_field);
        this.selfieImageUrlField1 = findViewById(R.id.selfie_url_1_text);
        this.selfieImageUrlField2 = findViewById(R.id.selfie_url_2_text);
        this.callbackUrlField = findViewById(R.id.callbackUrl_field);
        this.registerButton = findViewById(R.id.register_button);
        this.loadingBar = findViewById(R.id.indeterminateBar);
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
                    DialogUtils.openDialogToShowMessage(this,"Server Error Occurred", serverError.getErrorMessage());
                } else {
                    DialogUtils.openDialogToShowMessage(this, "Server Error Occurred");
                }
            } else {
                DialogUtils.openDialogToShowMessage(this, "An unexpected Error Occurred");
            }
        } else {
            DialogUtils.openDialogWithOkBtn(this, "Registration Success", "Document is successfully registered with us.", this);
        }
    }

    public void registerWithSelfie(View view) {
        SmilePassClient smilePassClient = ((MyApplication)getApplication()).getSmilePassClient();
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "JSON Going for selfie registration: "+registrationData.toString());
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
            jsonObject.put("callbackUrl",callbackURL); //optional
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(selfieImage1);
            jsonArray.put(selfieImage2);
            jsonObject.put("imageUrls", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    private void setFieldsEnabled (boolean enabled) {
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
