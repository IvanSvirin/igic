package com.cashback.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cashback.Utilities;
import com.cashback.rest.request.CouponsRequest;
import com.cashback.rest.request.MerchantsRequest;

/**
 * Created by ivansv on 16.04.2016.
 */
public class RestUtilities {
    public final static String TOKEN_ACCOUNT = "token_account";
    public final static String TOKEN_MERCHANTS = "token_merchants";
    public final static String TOKEN_COUPONS = "token_coupons";

    private final static long ACCOUNT_UPDATE_INTERVAL = 30000; // 30 sec * 1000
    private final static long MERCHANTS_UPDATE_INTERVAL = 21600000; // 6 h
    private final static long COUPONS_UPDATE_INTERVAL = 3600000; // 1h

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
            case TOKEN_MERCHANTS:
                if (differenceTime >= MERCHANTS_UPDATE_INTERVAL) {
                    new MerchantsRequest(context).fetchData();
                }
                break;
            case TOKEN_COUPONS:
                if (differenceTime >= COUPONS_UPDATE_INTERVAL) {
                    new CouponsRequest(context).fetchData();
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
