/**
 * @author Suraj Malviya.
 * Created on: December 18, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.smilepass.mobilesdk.exception.ClientException;
import com.smilepass.mobilesdk.exception.ServerException;
import com.smilepass.mobilesdk.listener.OnRegistrationResponseListener;
import com.smilepass.mobilesdk.main.SmilePassClient;
import com.smilepass.mobilesdk.model.ServerError;
import com.smilepass.smilepasssdksample.MyApplication;
import com.smilepass.smilepasssdksample.R;
import com.smilepass.smilepasssdksample.constants.AppConstants;
import com.smilepass.smilepasssdksample.utils.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentRegistrationActivity extends AppCompatActivity implements OnRegistrationResponseListener, AdapterView.OnItemSelectedListener, DialogInterface.OnClickListener {
    private final static String TAG = DocumentRegistrationActivity.class.getSimpleName();
    private Spinner documentChooser;
    private EditText uniqueKetField, frontUrlField, backUrlField;
    private Button documentSubmitButton;
    private ProgressBar loadingBar;
    private String selectedDocumentId = AppConstants.DRIVING_LICENSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_register);
        initView();
        initSpinner();
    }

    private void initView() {
        documentChooser = findViewById(R.id.documentSpinner);
        uniqueKetField = findViewById(R.id.uniqueKeyEditText);
        frontUrlField = findViewById(R.id.frontImageURLText);
        backUrlField = findViewById(R.id.backImageURLText);
        documentSubmitButton = findViewById(R.id.document_submit_button);
        loadingBar = findViewById(R.id.indeterminateBar);

        loadingBar.setVisibility(View.GONE);
    }

    private void initSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.documents, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentChooser.setAdapter(adapter);
        documentChooser.setOnItemSelectedListener(this);
    }

    public void registerWithDocument(View view) {
        SmilePassClient smilePassClient = ((MyApplication) getApplication()).getSmilePassClient();
        if (smilePassClient == null) {
            DialogUtils.openDialogToShowMessage(this, getString(R.string.smilepass_client_not_initialized));
            return;
        }
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "registerWithDocument(): Input JSON=" + registrationData.toString());
            smilePassClient.register(registrationData, this);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegistrationResponse(JSONObject jsonObject, Throwable throwable) {
        Log.d(TAG, "onRegistrationResponse(): jsonObject=" + jsonObject + ", throwable=" + throwable);
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
            boolean isSuccess = false;
            String message = null;
            if (jsonObject != null) {
                if (jsonObject.has("status")) {
                    try {
                        isSuccess = jsonObject.getBoolean("status");
                        if (!isSuccess) {
                            if (jsonObject.has("message"))
                                message = jsonObject.getString("message");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (isSuccess) {
                DialogUtils.openDialogWithOkBtn(this, getString(R.string.registration_success), getString(R.string.registration_success_message), this);
            } else {
                if (!TextUtils.isEmpty(message)) {
                    DialogUtils.openDialogWithOkBtn(this, getString(R.string.registration_error), message, null);
                } else {
                    DialogUtils.openDialogWithOkBtn(this, getString(R.string.registration_error), getString(R.string.registration_error_message), null);
                }
            }
        }
    }

    private JSONObject getRegistrationJson() {
        String uniqueKey = uniqueKetField.getText().toString();
        String frontImageUrl = frontUrlField.getText().toString();
        String backImageUrl = backUrlField.getText().toString();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("step", "document");
            jsonObject.put("uniqueKey", uniqueKey);
            jsonObject.put("documentId", this.selectedDocumentId);
            if (!TextUtils.isEmpty(frontImageUrl)) {
                jsonObject.put("frontImageUrl", frontImageUrl);
            }
            if (!TextUtils.isEmpty(backImageUrl)) {
                jsonObject.put("backImageUrl", backImageUrl);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        switch (position) {
            case 0:
                this.selectedDocumentId = AppConstants.DRIVING_LICENSE;
                break;
            case 1:
                this.selectedDocumentId = AppConstants.PASSPORT;
                break;
            default:
                this.selectedDocumentId = AppConstants.DRIVING_LICENSE;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setFieldsEnabled(boolean enabled) {
        this.backUrlField.setEnabled(enabled);
        this.frontUrlField.setEnabled(enabled);
        this.uniqueKetField.setEnabled(enabled);
        this.documentChooser.setEnabled(enabled);
        this.documentSubmitButton.setEnabled(enabled);
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        Intent successIntent = new Intent(this, SelfieRegistrationActivity.class);
        String uniqueKey = uniqueKetField.getText().toString();
        successIntent.putExtra("uniqueKey", uniqueKey);
        startActivity(successIntent);
    }
}
