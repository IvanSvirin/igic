package com.cashback.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cashback.Utilities;

/**
 * Created by ivansv on 16.04.2016.
 */
public class RestUtilities {
    public final static String TOKEN_ACCOUNT = "token_account";
    public final static String TOKEN_HOT_DEALS = "token_hot_deals";

    private final static long ACCOUNT_UPDATE_INTERVAL = 30000; // 30 sec * 1000
    private final static long HOT_DEALS_UPDATE_INTERVAL = 3600000; // 1h

    public static void syncDistantData(Context context, String token) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        long lastUpdateTime = pref.getLong(token, 0);
        long differenceTime = System.currentTimeMillis() - lastUpdateTime;
        switch (token) {
            case TOKEN_ACCOUNT:
                if (differenceTime >= ACCOUNT_UPDATE_INTERVAL) {
                    String userToken = Utilities.retrieveUserToken(context);
                    if (userToken != null) {
//                        new AccountRequest(context, userToken).fetchData();
                    }
                }
                break;
            case TOKEN_HOT_DEALS:
                if (differenceTime >= HOT_DEALS_UPDATE_INTERVAL) {
//                    new HotDealsRequest(context).fetchData();
                }
                break;
        }
    }

    public static void updateTimeStamp(Context context, String token) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putLong(token, System.currentTimeMillis());
        editor.apply();
    }
}
