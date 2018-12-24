/**
 * @author Suraj Malviya.
 * Created on: December 18, 2018
 */

package com.smilepass.smilepasssdksample.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.smilepass.smilepasssdksample.R;

public class MainActivity extends AppCompatActivity {

    private DialogInterface.OnClickListener dialogClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

         dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int choice) {
                Intent navigationIntent;
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                        navigationIntent= new Intent(MainActivity.this, DocumentRegistrationActivity.class);
                        startActivity(navigationIntent);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        navigationIntent = new Intent(MainActivity.this, SelfieRegistrationActivity.class);
                        startActivity(navigationIntent);;

                }
            }
        };
    }


    public void onVerifyButtonClick(View view) {
        Intent navigationIntent = new Intent(this, VerificationActivity.class);
        startActivity(navigationIntent);
    }


    public void onRegisterButtonClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Would you like to upload a document first ?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }


}
