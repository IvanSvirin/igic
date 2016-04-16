package com.cashback.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.cashback.rest.RestUtilities;
import com.cashback.rest.event.AccountEvent;
import com.cashback.rest.event.HotDealsEvent;

import de.greenrobot.event.EventBus;

/**
 * Created by ivansv on 16.04.2016.
 */
public class DataInsertHandler extends BulkAsyncQueryHandler {
    public static boolean IS_FILLING_MERCHANT_TABLE = false;

    public final static int ACCOUNT_TOKEN = 100;
    public final static int HOT_DEALS_TOKEN = 200;

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
            case HOT_DEALS_TOKEN:
                RestUtilities.updateTimeStamp(context, RestUtilities.TOKEN_HOT_DEALS);
                EventBus.getDefault().post(new HotDealsEvent(true, null));
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
