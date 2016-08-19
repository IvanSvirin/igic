package com.cashback;

import android.support.multidex.MultiDexApplication;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.api.GoogleApiClient;


public class App extends MultiDexApplication {
    public static final String GA_TRACKER = "ga_tracker";
    private Tracker tracker;
    public static GoogleApiClient googleApiClient;

    synchronized public Tracker getDefaultTracker() {
        if (tracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            tracker = analytics.newTracker(GA_TRACKER);
        }
        return tracker;
    }
}
