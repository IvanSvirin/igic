package com.cashback.ui;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.gcm.RegistrationGcmServices;
import com.cashback.ui.MainActivity;
import com.cashback.ui.login.LoginActivity;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by I.Svirin on 4/5/2016.
 */
public class LaunchActivity extends AppCompatActivity {
    public static final String MAIN_TAG_LOG = "igic_log";
    public static final String DB_TAG_LOG = "igic_db_log";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    //sandi_schleicher@hotmail.com
    //igive
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentRegistrationGCM;
        Intent intentNextActivity;

        Utilities.saveIdfa(this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        FacebookSdk.sdkInitialize(getApplicationContext());

        if (checkPlayServices()) {
            // Start intent to registration this app with GCM
            intentRegistrationGCM = new Intent(this, RegistrationGcmServices.class);
            startService(intentRegistrationGCM);
        }

        if (Utilities.isLoggedIn(this)) {
            intentNextActivity = new Intent(this, MainActivity.class);
        } else {
            intentNextActivity = new Intent(this, LoginActivity.class);
        }
        startActivity(intentNextActivity);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private boolean checkPlayServices() {
        int resultsCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultsCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultsCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultsCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.d(MAIN_TAG_LOG, "This device is not supported google play services.");
            }
        }
        return true;
    }
}
