package com.cashback.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.gcm.RegistrationGcmServices;
import com.cashback.ui.MainActivity;
import com.cashback.ui.account.HelpActivity;
import com.cashback.ui.login.LoginActivity;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

public class LaunchActivity extends AppCompatActivity {
    public static final String MAIN_TAG_LOG = "igic_log";
    public static final String DB_TAG_LOG = "igic_db_log";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intentRegistrationGCM;
        Intent intentNextActivity;

        Utilities.saveIdfa(this, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        // Automatic session tracking
        Branch.getAutoInstance(getApplicationContext());
        initBranchSession();

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
