package com.cashback.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;

import com.cashback.R;
import com.cashback.Utilities;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by I.Svirin on 4/5/2016.
 */
public class RegistrationGcmServices extends IntentService {
    private final static String SERVICE_NAME = "RegGcmServices";

    public RegistrationGcmServices() {
        super(SERVICE_NAME);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            synchronized (this) {
                InstanceID instanceID = InstanceID.getInstance(this);
                // it needs real gcm_senderId
                String token = instanceID.getToken(getString(R.string.gcm_senderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE);
                sendRegTokeToServer(token);
                Utilities.setStateSendingTokenToServer(this, true);
            }
        } catch (Exception e) {
            Utilities.setStateSendingTokenToServer(this, false);
        }
    }

    private void sendRegTokeToServer(String token) {

        AppCompatActivity activity = (AppCompatActivity) getBaseContext();
        // this code from S2C (must be changed later)
//        final MiscWorkerFragment fragment = MiscWorkerFragment.getWorkerFragment(activity.
//                getSupportFragmentManager());
//        MiscModel miscModel = fragment.getModel();
//        miscModel.registerGcm(token);
    }
}
