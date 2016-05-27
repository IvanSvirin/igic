package com.cashback.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.AccountEvent;
import com.cashback.rest.event.CategoriesEvent;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.rest.event.ExtrasEvent;
import com.cashback.rest.event.FavoritesEvent;
import com.cashback.rest.event.MerchantsEvent;
import com.cashback.rest.event.OrdersEvent;
import com.cashback.rest.event.PaymentsEvent;
import com.cashback.rest.event.ShoppingTripsEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by ivansv on 16.04.2016.
 */
public class DataInsertHandler extends BulkAsyncQueryHandler {
    public static boolean IS_FILLING_MERCHANT_TABLE = false;

    public final static int ACCOUNT_TOKEN = 100;
    public final static int MERCHANTS_TOKEN = 200;
    public final static int COUPONS_TOKEN = 300;
    public static final int CATEGORIES_TOKEN = 400;
    public static final int PAYMENTS_TOKEN = 500;
    public static final int SHOPPING_TRIPS_TOKEN = 600;
    public static final int ORDERS_TOKEN = 700;
    public static final int CHARITY_ORDERS_TOKEN = 701;
    public static final int EXTRAS_TOKEN = 800;
    public static final int FAVORITES_TOKEN = 900;

    private Context context;

    public DataInsertHandler(Context context, ContentResolver cr) {
        super(cr);
        this.context = context;
    }

    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
    }

    @Override
    protected void onInsertComplete(int token, Object cookie, Uri uri) {
        super.onInsertComplete(token, cookie, uri);
        switch (token) {
            case ACCOUNT_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_ACCOUNT);
                EventBus.getDefault().post(new AccountEvent(true, null));
                break;
        }
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        switch (token) {
            case ACCOUNT_TOKEN:
                EventBus.getDefault().post(new AccountEvent(true, null));
                break;
        }
    }

    @Override
    protected void onBulkInsertComplete(int token, Object cookie, int result) {
        super.onBulkInsertComplete(token, cookie, result);
        switch (token) {
            case COUPONS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_COUPONS);
                EventBus.getDefault().post(new CouponsEvent(true, null));
                break;
            case MERCHANTS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_MERCHANTS);
                EventBus.getDefault().post(new MerchantsEvent(true, null));
                break;
            case FAVORITES_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_FAVORITES);
                EventBus.getDefault().post(new FavoritesEvent(true, null));
                break;
            case EXTRAS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_EXTRAS);
                EventBus.getDefault().post(new ExtrasEvent(true, null));
                break;
            case CATEGORIES_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_CATEGORIES);
                EventBus.getDefault().post(new CategoriesEvent(true, null));
                break;
            case PAYMENTS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_PAYMENTS);
                EventBus.getDefault().post(new PaymentsEvent(true, null));
                break;
            case SHOPPING_TRIPS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_SHOPPING_TRIPS);
                EventBus.getDefault().post(new ShoppingTripsEvent(true, null));
                break;
            case ORDERS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_ORDERS);
                EventBus.getDefault().post(new OrdersEvent(true, null));
                break;
            case CHARITY_ORDERS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_CHARITY_ORDERS);
                EventBus.getDefault().post(new OrdersEvent(true, null));
                break;
        }
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        switch (token) {
        }
    }
}
