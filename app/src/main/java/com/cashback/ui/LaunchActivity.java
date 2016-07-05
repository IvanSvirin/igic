package com.cashback.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

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
    public static final String GA_TRACKER = "ga_tracker";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    static final String TAG = "GCM Registration";
    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(GA_TRACKER);
        }
        return tracker;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentRegistrationGCM;
        Intent intentNextActivity;
        Utilities.saveIdfa(this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
        //Google Analytics
        tracker = getDefaultTracker();
        tracker.setScreenName("");
        tracker.send(new HitBuilders.ScreenViewBuilder().build());
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
                        String vendorId = referringParams.getString("vendor_id");
                        if (vendorId != null) {
                            Intent intent = new Intent(getApplicationContext(), StoreActivity.class);
                            intent.putExtra("vendor_id", Long.parseLong(vendorId));
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, this.getIntent().getData(), this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    public static void shareLink(final Context context, String url, long vendorId) {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .addContentMetadata("vendor_id", String.valueOf(vendorId));
//                .setCanonicalIdentifier("share");
//                .setTitle("Check out this article!")
//                .setContentDescription("It’s really entertaining...")
//                .setContentImageUrl("https://mysite.com/article_logo.png")
//                .addContentMetadata("read_progress", "17%");

        LinkProperties linkProperties = new LinkProperties()
//                .setChannel("facebook")
//                .setFeature("sharing")
                .addControlParameter("$fallback_url", url);

        branchUniversalObject.generateShortUrl(context, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, url);
                context.startActivity(Intent.createChooser(share, "Share Text"));
            }
        });
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
