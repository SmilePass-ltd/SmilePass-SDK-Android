/**
 * @author Suraj Malviya.
 * Created on: December 18, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.smilepass.smilepasssdksample.util.DialogUtils;

import org.json.JSONException;
import org.json.JSONObject;

public class DocumentRegistrationActivity extends AppCompatActivity implements OnRegistrationResponseListener, AdapterView.OnItemSelectedListener, DialogInterface.OnClickListener {

    private Spinner documentChooser;
    private EditText uniqueKetField, frontUrlField, backUrlField;
    private Button documentSubmitButton;
    private ProgressBar loadingBar;

    private String selectedDocumentId = "3";
    private final String TAG = "DOCUMENT REGISTRATION: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_register);

        initResources();

        //TODO: Comment the below two lines for build/QA.
        this.frontUrlField.setText("https://firebasestorage.googleapis.com/v0/b/myproject-946ef.appspot.com/o/drivinglicences%2FIMG_2481.JPG?alt=media&token=7f572a31-f351-4ea2-ade6-189ff10f4823");
        this.backUrlField.setText("https://firebasestorage.googleapis.com/v0/b/myproject-946ef.appspot.com/o/drivinglicences%2FIMG_2482.JPG?alt=media&token=3ec70151-c813-40ec-9310-8b3f72c44dcd");

        //hiding progress bar.
        loadingBar.setVisibility(View.GONE);

        //Setup spinner adapter.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.documents, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        documentChooser.setAdapter(adapter);
        documentChooser.setOnItemSelectedListener(this);
    }

    private void initResources() {
        this.documentChooser = findViewById(R.id.documentSpinner);
        this.uniqueKetField = findViewById(R.id.uniqueKeyEditText);
        this.frontUrlField = findViewById(R.id.frontImageURLText);
        this.backUrlField = findViewById(R.id.backImageURLText);
        this.documentSubmitButton = findViewById(R.id.document_submit_button);
        this.loadingBar = findViewById(R.id.indeterminateBar);
    }

    public void registerWithDocument(View view) {
        SmilePassClient smilePassClient = ((MyApplication)getApplication()).getSmilePassClient();
        JSONObject registrationData = getRegistrationJson();
        try {
            setFieldsEnabled(false);
            loadingBar.setVisibility(View.VISIBLE);
            Log.d(TAG, "JSON Going: "+registrationData.toString());
            smilePassClient.register(registrationData, this);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRegistrationResponse(JSONObject jsonObject, Throwable throwable) {
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
            DialogUtils.openDialogWithOkBtn(this, "Registration Success", "Document is successfully registered with us.", this);
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
            jsonObject.put("frontImageUrl", frontImageUrl);
            jsonObject.put("backImageUrl", backImageUrl);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i){
            case 0: this.selectedDocumentId = "3";
                break;
            case 1: this.selectedDocumentId = "2";
                break;
            default: this.selectedDocumentId = "3";
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void setFieldsEnabled (boolean enabled) {
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
