package com.cashback;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONObject;

import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by I.Svirin on 6/2/2016.
 */
public class BranchActivity extends AppCompatActivity {
    @Override
    public void onStart() {
        super.onStart();

        Branch branch = Branch.getInstance();
        branch.initSession(new Branch.BranchReferralInitListener() {
            @Override
            public void onInitFinished(JSONObject referringParams, BranchError error) {
                if (error == null) {
                    // params are the deep linked params associated with the link that the user clicked before showing up
                    Log.i("BranchConfigTest", "deep link data: " + referringParams.toString());
                }
            }
        }, this.getIntent().getData(), this);


        createLink();
    }

    @Override
    public void onNewIntent(Intent intent) {
        this.setIntent(intent);
    }

    private void createLink() {
        BranchUniversalObject branchUniversalObject = new BranchUniversalObject()
                .setCanonicalIdentifier("article/12345")
                .setTitle("Check out this article!")
                .setContentDescription("It’s really entertaining...")
                .setContentImageUrl("https://mysite.com/article_logo.png")
                .addContentMetadata("read_progress", "17%");


        LinkProperties linkProperties = new LinkProperties()
                .setChannel("facebook")
                .setFeature("sharing")
                .addControlParameter("$fallback_url", "https://beta1.igive.com/html/fm.cfm?vendorid=1384&s=148&afsrc=1");

        branchUniversalObject.generateShortUrl((Activity) this, linkProperties, new Branch.BranchLinkCreateListener() {
            @Override
            public void onLinkCreate(String url, BranchError error) {
                Log.i("MyApp", "Got my Branch link to share: " + url);
            }
        });
    }
}
