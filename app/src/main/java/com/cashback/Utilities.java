package com.cashback;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by I.Svirin on 4/5/2016.
 */
public class Utilities {
    private final static String PREF_TOKEN_KEY = "pref_token";
    private static final String PREF_ENTRY_KEY = "pref_entry";


    public static String retrieveUserToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_TOKEN_KEY, null);
    }

    public static boolean retrieveUserEntry(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_ENTRY_KEY, false);
    }
    public static boolean isLoggedIn(Context context) {
        return !TextUtils.isEmpty(retrieveUserToken(context)) && retrieveUserEntry(context);
    }

    public static void setStateSendingTokenToServer(Context context, final boolean state){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sentTokenToServer", state);
        editor.apply();
    }

    public static boolean isActiveConnection(Context context) {
        ConnectivityManager mng = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = mng.getActiveNetworkInfo();
        if (net != null && net.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isShowTour(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_show_tour_key), true);
    }

    public static void setShowTour(Context context, final boolean toShow) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_show_tour_key), toShow);
        editor.apply();
    }
}
