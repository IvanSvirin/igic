package com.cashback;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import db.DataContract;
import io.branch.indexing.BranchUniversalObject;
import io.branch.referral.Branch;
import io.branch.referral.BranchError;
import io.branch.referral.util.LinkProperties;

/**
 * Created by I.Svirin on 7/11/2016.
 */
public class App extends MultiDexApplication {
    public static final String GA_TRACKER = "ga_tracker";
    private Tracker tracker;

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(GA_TRACKER);
        }
        return tracker;
    }
}
