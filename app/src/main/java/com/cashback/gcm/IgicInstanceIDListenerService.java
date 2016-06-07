package com.cashback.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class IgicInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Intent intentRegistrationGCM = new Intent(this, RegistrationGcmServices.class);
        startService(intentRegistrationGCM);
    }
}
