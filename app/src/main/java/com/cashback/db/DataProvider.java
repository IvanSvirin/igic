package com.cashback.db;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * Created by ivansv on 16.04.2016.
 */
public class DataProvider extends ContentProvider {
    private static final int MERCHANTS = 100;
    private static final int MERCHANT_BY_ID = 101;
    private static final int MERCHANTS_BY_IDS = 102;
    private static final int FAVORITE_MERCHANTS = 200;
    private static final int EXTRA_MERCHANTS =300;


    private String LOG_TAG = "sql_log";
    private DbHelper dbHelper;
    private Context context;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants", MERCHANTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants/#", MERCHANT_BY_ID);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants/*", MERCHANTS_BY_IDS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "favorite_merchants", FAVORITE_MERCHANTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "extra_merchants", EXTRA_MERCHANTS);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new DbHelper(getContext());
        context = getContext();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        Cursor cursor = null;
        String common_selection = "";
        switch (match) {
            case MERCHANTS:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = DataContract.Merchants.COLUMN_NAME + " COLLATE NOCASE ASC";
                cursor = db.query(DataContract.Merchants.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                return cursor;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        return super.bulkInsert(uri, values);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }
}
