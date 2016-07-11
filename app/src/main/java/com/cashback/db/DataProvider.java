package com.cashback.db;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import db.DataContract;

public class DataProvider extends ContentProvider {
    private static final int MERCHANTS = 100;
    private static final int MERCHANT_BY_ID = 101;
    private static final int MERCHANTS_BY_IDS = 102;
    private static final int COUPONS = 200;
    private static final int COUPON_BY_ID = 201;
    private static final int FAVORITES = 300;
    private static final int FAVORITES_BY_ID = 301;
    private static final int EXTRAS = 400;
    private static final int EXTRAS_BY_ID = 401;

    private static final int CATEGORIES = 500;
    private static final int PAYMENTS = 600;
    private static final int SHOPPING_TRIPS = 700;
    private static final int ORDERS = 800;
    private static final int CHARITY_ORDERS = 801;

    private static final int CHARITY_ACCOUNTS = 901;
    private static final int CASHBACK_ACCOUNTS = 902;
    private static final int MISC = 903;


    private String LOG_TAG = "sql_log";
    private DbHelper dbHelper;
    private Context context;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants", MERCHANTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants/#", MERCHANT_BY_ID);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "merchants/*", MERCHANTS_BY_IDS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "coupons", COUPONS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "coupons/#", COUPON_BY_ID);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "extras", EXTRAS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "extras/#", EXTRAS_BY_ID);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "favorites", FAVORITES);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "favorites/#", FAVORITES_BY_ID);

        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "categories", CATEGORIES);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "payments", PAYMENTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "shopping_trips", SHOPPING_TRIPS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "orders", ORDERS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "charity_orders", CHARITY_ORDERS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "charity_accounts", CHARITY_ACCOUNTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "cashback_accounts", CASHBACK_ACCOUNTS);
        uriMatcher.addURI(DataContract.CONTENT_AUTHORITY, "misc", MISC);
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
            case MERCHANT_BY_ID:
                common_selection = DataContract.Merchants.COLUMN_VENDOR_ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    common_selection = selection + " AND " + common_selection;
                cursor = db.query(DataContract.Merchants.TABLE_NAME, projection, common_selection, null, null, null, null);
                break;
            case MERCHANTS_BY_IDS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Merchants.COLUMN_NAME;
                }
                common_selection = DataContract.Merchants.COLUMN_VENDOR_ID + " IN (" + uri.getLastPathSegment() + ")";
                if (!TextUtils.isEmpty(selection)) {
                    common_selection = selection + " AND " + common_selection;
                }
                cursor = db.query(true, DataContract.Merchants.TABLE_NAME, projection, common_selection, null, null, null, sortOrder, null);
                break;
            case FAVORITES:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = DataContract.Favorites.COLUMN_NAME + " COLLATE NOCASE ASC";
                cursor = db.query(DataContract.Favorites.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                return cursor;
            case FAVORITES_BY_ID:
                common_selection = DataContract.Favorites.COLUMN_VENDOR_ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    common_selection = selection + " AND " + common_selection;
                cursor = db.query(DataContract.Favorites.TABLE_NAME, projection, common_selection, null, null, null, null);
                break;
            case EXTRAS:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = DataContract.Extras.COLUMN_NAME + " COLLATE NOCASE ASC";
                cursor = db.query(DataContract.Extras.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                return cursor;
            case EXTRAS_BY_ID:
                common_selection = DataContract.Extras.COLUMN_VENDOR_ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    common_selection = selection + " AND " + common_selection;
                cursor = db.query(DataContract.Extras.TABLE_NAME, projection, common_selection, null, null, null, null);
                break;
            case COUPONS:
                if (TextUtils.isEmpty(sortOrder))
                    sortOrder = DataContract.Coupons.COLUMN_VENDOR_ID + " COLLATE NOCASE ASC";
                cursor = db.query(DataContract.Coupons.TABLE_NAME, projection, selection, null, null, null, sortOrder);
                return cursor;
            case COUPON_BY_ID:
                common_selection = DataContract.Coupons.COLUMN_COUPON_ID + " = " + uri.getLastPathSegment();
                if (!TextUtils.isEmpty(selection))
                    common_selection = selection + " AND " + common_selection;
                cursor = db.query(DataContract.Coupons.TABLE_NAME, projection, common_selection, null, null, null, null);
                break;
            case CATEGORIES:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Categories._ID + " COLLATE NOCASE ASC";
                }
                cursor = db.query(DataContract.Categories.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case PAYMENTS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Payments.COLUMN_PAYMENT_DATE + " COLLATE NOCASE ASC";
                }
                cursor = db.query(DataContract.Payments.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case SHOPPING_TRIPS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.ShoppingTrips.COLUMN_TRIP_DATE + " COLLATE NOCASE DESC";
                }
                cursor = db.query(DataContract.ShoppingTrips.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case ORDERS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Orders.COLUMN_ORDER_DATE + " COLLATE NOCASE DESC";
                }
                cursor = db.query(DataContract.Orders.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case CHARITY_ORDERS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.CharityOrders.COLUMN_ORDER_DATE + " COLLATE NOCASE DESC";
                }
                cursor = db.query(DataContract.CharityOrders.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case CHARITY_ACCOUNTS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.CharityAccounts.COLUMN_TOKEN + " COLLATE NOCASE ASC";
                }
                cursor = db.query(DataContract.CharityAccounts.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case CASHBACK_ACCOUNTS:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.CashbackAccounts.COLUMN_TOKEN + " COLLATE NOCASE ASC";
                }
                cursor = db.query(DataContract.CashbackAccounts.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            case MISC:
                if (TextUtils.isEmpty(sortOrder)) {
                    sortOrder = DataContract.Misc.COLUMN_SHARE_DEAL_TEXT + " COLLATE NOCASE ASC";
                }
                cursor = db.query(DataContract.Misc.TABLE_NAME, projection, null, null, null, null, sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        ContentResolver resolver = context.getContentResolver();
        long rowID;
        Uri resultUri = null;
        switch (match) {
            case MERCHANTS:
                rowID = db.insertWithOnConflict(DataContract.Merchants.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_MERCHANTS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case FAVORITES:
                rowID = db.insertWithOnConflict(DataContract.Favorites.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_FAVORITES, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case EXTRAS:
                rowID = db.insertWithOnConflict(DataContract.Extras.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_EXTRAS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case COUPONS:
                rowID = db.insertWithOnConflict(DataContract.Coupons.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_COUPONS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CATEGORIES:
                rowID = db.insertWithOnConflict(DataContract.Categories.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_CATEGORIES, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case PAYMENTS:
                rowID = db.insertWithOnConflict(DataContract.Payments.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_PAYMENTS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case SHOPPING_TRIPS:
                rowID = db.insertWithOnConflict(DataContract.ShoppingTrips.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_SHOPPING_TRIPS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case ORDERS:
                rowID = db.insertWithOnConflict(DataContract.Orders.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_ORDERS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CHARITY_ORDERS:
                rowID = db.insertWithOnConflict(DataContract.CharityOrders.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_CHARITY_ORDERS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CHARITY_ACCOUNTS:
                rowID = db.insertWithOnConflict(DataContract.CharityAccounts.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_CHARITY_ACCOUNTS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case CASHBACK_ACCOUNTS:
                rowID = db.insertWithOnConflict(DataContract.CashbackAccounts.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_CASHBACK_ACCOUNTS, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            case MISC:
                rowID = db.insertWithOnConflict(DataContract.Misc.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                if (rowID > 0) {
                    resultUri = ContentUris.withAppendedId(DataContract.URI_MISC, rowID);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        resolver.notifyChange(resultUri, null);
        return resultUri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        int countInsert = 0;
        switch (match) {
            case MERCHANTS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case FAVORITES:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case EXTRAS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case COUPONS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case CATEGORIES:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case PAYMENTS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case SHOPPING_TRIPS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case ORDERS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case CHARITY_ORDERS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case CHARITY_ACCOUNTS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case CASHBACK_ACCOUNTS:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
            case MISC:
                db.beginTransaction();
                try {
                    delete(uri, null, null);
                    for (ContentValues val : values) {
                        insert(uri, val);
                        countInsert++;
                    }
                    db.setTransactionSuccessful();
                } catch (SQLException e) {
                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    db.endTransaction();
                }
                break;
        }
        context.getContentResolver().notifyChange(uri, null);
        return countInsert;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = dbHelper.getWritableDatabase();
        final int match = uriMatcher.match(uri);
        String id;
        int affectedRowsCount;
        switch (match) {
            case MERCHANTS:
                affectedRowsCount = db.delete(DataContract.Merchants.TABLE_NAME, null, null);
                break;
            case FAVORITES:
                affectedRowsCount = db.delete(DataContract.Favorites.TABLE_NAME, null, null);
                break;
            case EXTRAS:
                affectedRowsCount = db.delete(DataContract.Extras.TABLE_NAME, null, null);
                break;
            case COUPONS:
                affectedRowsCount = db.delete(DataContract.Coupons.TABLE_NAME, null, null);
                break;
            case CATEGORIES:
                affectedRowsCount = db.delete(DataContract.Categories.TABLE_NAME, null, null);
                break;
            case PAYMENTS:
                affectedRowsCount = db.delete(DataContract.Payments.TABLE_NAME, null, null);
                break;
            case SHOPPING_TRIPS:
                affectedRowsCount = db.delete(DataContract.ShoppingTrips.TABLE_NAME, null, null);
                break;
            case ORDERS:
                affectedRowsCount = db.delete(DataContract.Orders.TABLE_NAME, null, null);
                break;
            case CHARITY_ORDERS:
                affectedRowsCount = db.delete(DataContract.CharityOrders.TABLE_NAME, null, null);
                break;
            case CHARITY_ACCOUNTS:
                affectedRowsCount = db.delete(DataContract.CharityAccounts.TABLE_NAME, null, null);
                break;
            case CASHBACK_ACCOUNTS:
                affectedRowsCount = db.delete(DataContract.CashbackAccounts.TABLE_NAME, null, null);
                break;
            case MISC:
                affectedRowsCount = db.delete(DataContract.Misc.TABLE_NAME, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        context.getContentResolver().notifyChange(uri, null);
        return affectedRowsCount;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case MERCHANTS:
                return DataContract.Merchants.CONTENT_TYPE;
            case FAVORITES:
                return DataContract.Favorites.CONTENT_TYPE;
            case EXTRAS:
                return DataContract.Extras.CONTENT_TYPE;
            case MERCHANT_BY_ID:
                return DataContract.Merchants.CONTENT_ITEM_TYPE;
            case MERCHANTS_BY_IDS:
                return DataContract.Merchants.CONTENT_ITEM_TYPE;
            case COUPONS:
                return DataContract.Coupons.CONTENT_TYPE;
            case COUPON_BY_ID:
                return DataContract.Coupons.CONTENT_ITEM_TYPE;
            case CATEGORIES:
                return DataContract.Categories.CONTENT_TYPE;
            case PAYMENTS:
                return DataContract.Payments.CONTENT_TYPE;
            case SHOPPING_TRIPS:
                return DataContract.ShoppingTrips.CONTENT_TYPE;
            case ORDERS:
                return DataContract.Orders.CONTENT_TYPE;
            case CHARITY_ORDERS:
                return DataContract.CharityOrders.CONTENT_TYPE;
            case CHARITY_ACCOUNTS:
                return DataContract.CharityAccounts.CONTENT_TYPE;
            case CASHBACK_ACCOUNTS:
                return DataContract.CashbackAccounts.CONTENT_TYPE;
            case MISC:
                return DataContract.Misc.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
    }
}
