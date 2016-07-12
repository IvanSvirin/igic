package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.cashback.App;
import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.gcm.RegistrationGcmServices;
import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;
import ui.MainActivity;

public class LaunchActivity extends AppCompatActivity {
    public static final String MAIN_TAG_LOG = "igic_log";
    public static final String DB_TAG_LOG = "igic_db_log";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Registration";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentRegistrationGCM;
        Intent intentNextActivity;
        Utilities.saveIdfa(this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//         Automatic session tracking
        Branch.getAutoInstance(getApplicationContext());
        initBranchSession();

        FacebookSdk.sdkInitialize(getApplicationContext());

        if (checkPlayServices()) {
            // Start intent to registration this app with GCM
            intentRegistrationGCM = new Intent(this, RegistrationGcmServices.class);
            startService(intentRegistrationGCM);
        }

        intentNextActivity = new Intent(this, MainActivity.class);
        startActivity(intentNextActivity);
        finish();
    }

    private void initBranchSession() {
        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    try {
                        String vendorId = referringParams.getString("BRANCH_MERCHANT_ID");
                        if (vendorId != null) {
                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            intent.putExtra("BRANCH_MERCHANT_ID", Long.parseLong(vendorId));
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this.getIntent().getData(), this);
    }

    public static void shareMerchantLink(final Context context, String url, long vendorId, String imageUrl) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .addContentMetadata("BRANCH_MERCHANT_ID", String.valueOf(vendorId))
                .addContentMetadata("BRANCH_REFERBY", Utilities.retrieveEmail(context))
                .setTitle(Utilities.retrieveShareDealText(context))
                .setContentDescription(Utilities.retrieveShareDealText(context))
                .setContentImageUrl(imageUrl);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("SHARE_VIA_SHARING_DIALOG")
                .setFeature(context.getString(R.string.app_name) + ".EVENT_SHARE_STORE")
                .addControlParameter("BRANCH_LINK", url);

        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, Utilities.retrieveShareDealText(context) + "\n" + url);
                context.startActivity(Intent.createChooser(share, Utilities.retrieveShareDealText(context)));
            }
        });
    }

    public static void shareDealLink(final Context context, String url, long vendorId, long couponId, String imageUrl) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .addContentMetadata("BRANCH_MERCHANT_ID", String.valueOf(vendorId))
                .addContentMetadata("BRANCH_COUPON_ID", String.valueOf(couponId))
                .addContentMetadata("BRANCH_REFERBY", Utilities.retrieveEmail(context))
                .setTitle(Utilities.retrieveShareDealText(context))
                .setContentDescription(Utilities.retrieveShareDealText(context))
                .setContentImageUrl(imageUrl);

        LinkProperties linkProperties = new LinkProperties()
                .setChannel("SHARE_VIA_SHARING_DIALOG")
                .setFeature(context.getString(R.string.app_name) + ".EVENT_SHARE_DEAL")
                .addControlParameter("BRANCH_LINK", url);

        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, Utilities.retrieveShareDealText(context) + "\n" + url);
                context.startActivity(Intent.createChooser(share, Utilities.retrieveShareDealText(context)));
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog((android.app.Activity) this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }
}
