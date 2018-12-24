/**
 * @author Suraj Malviya.
 * Date: December 18, 2018
 */

package com.smilepass.smilepasssdksample;

import android.app.Application;

import com.smilepass.mobilesdk.exception.ClientException;
import com.smilepass.mobilesdk.main.SmilePassClient;
import com.smilepass.mobilesdk.main.SmilePassRestClient;

public class MyApplication extends Application {

    private SmilePassClient smilePassClient;

    private final String smilePassApiKey = "YOUR API KEY";

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            this.smilePassClient = new SmilePassRestClient(smilePassApiKey, "SMILE PASS SERVER URL");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public SmilePassClient getSmilePassClient() {
        return smilePassClient;
    }
}
