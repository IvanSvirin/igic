package com.cashback.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.AccountEvent;
import com.cashback.rest.event.CategoriesEvent;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.rest.event.MerchantsEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by ivansv on 16.04.2016.
 */
public class DataInsertHandler extends BulkAsyncQueryHandler {
    public static boolean IS_FILLING_MERCHANT_TABLE = false;

    public final static int ACCOUNT_TOKEN = 100;
    public final static int MERCHANTS_TOKEN = 200;
    public final static int COUPONS_TOKEN = 300;
    public static final int CATEGORY_TOKEN = 400;


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
            case CATEGORY_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_CATEGORIES);
                EventBus.getDefault().post(new CategoriesEvent(true, null));
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
