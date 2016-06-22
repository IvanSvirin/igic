package com.cashback.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;
import com.google.firebase.iid.FirebaseInstanceId;

public class IgicInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        Intent intentRegistrationGCM = new Intent(this, RegistrationGcmServices.class);
        startService(intentRegistrationGCM);
    }
}
