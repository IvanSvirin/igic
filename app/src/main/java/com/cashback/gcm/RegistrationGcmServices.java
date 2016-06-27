package com.cashback.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.rest.request.GcmRequest;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;


public class RegistrationGcmServices extends IntentService {
    private final static String SERVICE_NAME = "RegGcmServices";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public RegistrationGcmServices() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
//            String token = instanceID.getToken(getString(R.string.project_number), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
            sendRegTokenToServer(token);
            Utilities.setStateSendingTokenToServer(this, true);
        } catch (Exception e) {
            Utilities.setStateSendingTokenToServer(this, false);
        }

        Intent registrationComplete = new Intent(REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegTokenToServer(String token) {
        new GcmRequest(this).onGcm(token);
    }
}
