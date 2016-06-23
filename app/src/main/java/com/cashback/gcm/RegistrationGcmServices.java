package com.cashback.gcm;

import android.app.IntentService;
import android.content.Intent;

import com.cashback.R;
import com.cashback.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class RegistrationGcmServices extends IntentService {
    private final static String SERVICE_NAME = "RegGcmServices";

    public RegistrationGcmServices() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        try {
//            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//            String token = gcm.register(getString(R.string.project_number));
//            sendRegTokeToServer(token);
//            Utilities.setStateSendingTokenToServer(getApplicationContext(), true);
//        } catch (IOException e) {
//            e.printStackTrace();
//            Utilities.setStateSendingTokenToServer(getApplicationContext(), false);
//        }

        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.project_number), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            sendRegTokeToServer(token);
            Utilities.setStateSendingTokenToServer(this, true);
        } catch (Exception e) {
            Utilities.setStateSendingTokenToServer(this, false);
        }
    }

    private void sendRegTokeToServer(String token) {
    }
}
