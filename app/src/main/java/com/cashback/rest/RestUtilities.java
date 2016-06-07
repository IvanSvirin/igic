package com.cashback.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cashback.Utilities;
import com.cashback.rest.request.CategoriesRequest;
import com.cashback.rest.request.CharityOrdersRequest;
import com.cashback.rest.request.CouponsRequest;
import com.cashback.rest.request.ExtrasRequest;
import com.cashback.rest.request.FavoritesRequest;
import com.cashback.rest.request.MerchantsRequest;
import com.cashback.rest.request.OrdersRequest;
import com.cashback.rest.request.PaymentsRequest;
import com.cashback.rest.request.ShoppingTripsRequest;

public class RestUtilities {
    public final static String TOKEN_ACCOUNT = "token_account";
    public final static String TOKEN_MERCHANTS = "token_merchants";
    public final static String TOKEN_FAVORITES = "token_favorites";
    public final static String TOKEN_EXTRAS = "token_extras";
    public final static String TOKEN_COUPONS = "token_coupons";
    public final static String TOKEN_CATEGORIES = "token_categories";
    public final static String TOKEN_PAYMENTS = "token_payments";
    public static final String TOKEN_SHOPPING_TRIPS = "token_shopping_trips";
    public static final String TOKEN_ORDERS = "token_orders";
    public static final String TOKEN_CHARITY_ORDERS = "token_charity_orders";

    private final static long ACCOUNT_UPDATE_INTERVAL = 30000; // 30 sec * 1000
    private final static long EXTRAS_UPDATE_INTERVAL = 21600000; // 6 h
    private final static long MERCHANTS_UPDATE_INTERVAL = 21600000; // 6 h
    private final static long FAVORITES_UPDATE_INTERVAL = 21600000; // 6 h
    private final static long COUPONS_UPDATE_INTERVAL = 3600000; // 1h
    private final static long CATEGORIES_UPDATE_INTERVAL = 43200000; // 12h
    private final static long PAYMENTS_UPDATE_INTERVAL = 43200000; // 12h
    private static final long SHOPPING_TRIPS_UPDATE_INTERVAL = 43200000; // 12h
    private static final long ORDERS_UPDATE_INTERVAL = 43200000; // 12h
    private static final long CHARITY_ORDERS_UPDATE_INTERVAL = 43200000; // 12h

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
            case TOKEN_FAVORITES:
                if (differenceTime >= FAVORITES_UPDATE_INTERVAL) {
                    new FavoritesRequest(context).fetchData();
                }
                break;
            case TOKEN_EXTRAS:
                if (differenceTime >= EXTRAS_UPDATE_INTERVAL) {
                    new ExtrasRequest(context).fetchData();
                }
                break;
            case TOKEN_COUPONS:
                if (differenceTime >= COUPONS_UPDATE_INTERVAL) {
                    new CouponsRequest(context).fetchData();
                }
                break;
            case TOKEN_CATEGORIES:
                if (differenceTime >= CATEGORIES_UPDATE_INTERVAL) {
                    new CategoriesRequest(context).fetchData();
                }
                break;
            case TOKEN_PAYMENTS:
                if (differenceTime >= PAYMENTS_UPDATE_INTERVAL) {
                    new PaymentsRequest(context).fetchData();
                }
                break;
            case TOKEN_SHOPPING_TRIPS:
                if (differenceTime >= SHOPPING_TRIPS_UPDATE_INTERVAL) {
                    new ShoppingTripsRequest(context).fetchData();
                }
                break;
            case TOKEN_ORDERS:
                if (differenceTime >= ORDERS_UPDATE_INTERVAL) {
                    new OrdersRequest(context).fetchData();
                }
                break;
            case TOKEN_CHARITY_ORDERS:
                if (differenceTime >= CHARITY_ORDERS_UPDATE_INTERVAL) {
                    new CharityOrdersRequest(context).fetchData();
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
