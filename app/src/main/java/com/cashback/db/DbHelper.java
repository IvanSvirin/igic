package com.cashback.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper{
    public final static int DATABASE_VERSION = 1;
    public final static String DATABASE_NAME = "igic.db";
    private final AuxiliaryDataDbHelper innerHelper;

    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        innerHelper = new AuxiliaryDataDbHelper();
    }

    public DbHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler);
        innerHelper = new AuxiliaryDataDbHelper();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(innerHelper.SQL_CREATE_MERCHANTS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_FAVORITES_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_EXTRAS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_COUPONS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_CATEGORIES_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_PAYMENTS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_SHOPPING_TRIPS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_ORDERS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_CHARITY_ORDERS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_CHARITY_ACCOUNTS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_CASHBACK_ACCOUNTS_TABLE);
        db.execSQL(innerHelper.SQL_CREATE_MISC_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is normal upgrade policy for db that only cache the online data
        db.execSQL(innerHelper.SQL_DELETE_MERCHANTS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_FAVORITES_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_EXTRAS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_COUPONS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_CATEGORIES_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_PAYMENTS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_SHOPPING_TRIPS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_ORDERS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_CHARITY_ORDERS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_CHARITY_ACCOUNTS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_CASHBACK_ACCOUNTS_TABLE);
        db.execSQL(innerHelper.SQL_DELETE_MISC_TABLE);
        onCreate(db);
    }
}
