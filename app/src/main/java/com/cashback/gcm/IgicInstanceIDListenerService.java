package com.cashback.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by I.Svirin on 4/5/2016.
 */
public class IgicInstanceIDListenerService extends InstanceIDListenerService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        Intent intentRegistrationGCM = new Intent(this, RegistrationGcmServices.class);
        startService(intentRegistrationGCM);
    }
}
