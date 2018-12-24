/**
 * @author Suraj Malviya.
 * Created on: December 19, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.smilepass.smilepasssdksample.util.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class VerificationActivity extends AppCompatActivity implements OnVerificationResponseListener {

    private EditText uniqueKeyField, selfieImageUrlField;
    private Button verifyButton;

    private ProgressBar loadingBar;

    private final String TAG = "VERIFY USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_user);

        initResources();

        //hiding progress bar.
        loadingBar.setVisibility(View.GONE);

        selfieImageUrlField.setText("https://ga-core.s3.amazonaws.com/production/uploads/instructor/image/14769/thumb_AAEAAQAAAAAAAAH_AAAAJDE0M2I3OWMzLTM1MGItNDE2OC04MjNkLWYxZmNiMTQyNmU1Zg.jpg");
    }

    private void initResources() {
        uniqueKeyField = findViewById(R.id.verify_uniquekey_field);
        selfieImageUrlField = findViewById(R.id.selfie_image_url);
        verifyButton = findViewById(R.id.verifyButton);
        loadingBar = findViewById(R.id.verifyUser_indeterminateBar);
    }

    public void verifyUserClick(View view) {
        SmilePassClient smilePassClient = ((MyApplication)getApplication()).getSmilePassClient();
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "JSON Going: "+registrationData.toString());
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
        setFieldsEnabled(true);
        loadingBar.setVisibility(View.GONE);

        if (throwable != null) {
            if (throwable instanceof ServerException) {
                ServerError serverError = ((ServerException) throwable).error;
                if (serverError.getErrorMessage() != null)
                    DialogUtils.openDialogToShowMessage(this,"Server Error Occurred", serverError.getErrorMessage());
                else
                    DialogUtils.openDialogToShowMessage(this, "Server Error Occurred");
            } else {
                DialogUtils.openDialogToShowMessage(this, "An unexpected Error Occurred");
            }
        } else {
            try {
                String confidenceScore = jsonObject.getString("confidenceScore");
                DialogUtils.openDialogToShowMessage(this, "Verification Success", "User is successfully verified with confidence score: "+confidenceScore);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    private void setFieldsEnabled (boolean enabled) {
        this.uniqueKeyField.setEnabled(enabled);
        this.selfieImageUrlField.setEnabled(enabled);
        this.verifyButton.setEnabled(enabled);
    }

}
