package com.cashback;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

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
}
