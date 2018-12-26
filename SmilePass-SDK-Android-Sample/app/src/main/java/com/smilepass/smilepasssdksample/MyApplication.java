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

    private final static String API_KEY = "{{API KEY}}";
    private final static String SMILE_PASS_SERVER_URL = "{{SERVER_UR}}";
    private SmilePassClient smilePassClient;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            this.smilePassClient = new SmilePassRestClient(API_KEY, SMILE_PASS_SERVER_URL);
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public SmilePassClient getSmilePassClient() {
        return smilePassClient;
    }
}
