package com.cashback.db;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ivansv on 16.04.2016.
 */
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
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This is normal upgrade policy for db that only cache the online data
        db.execSQL(innerHelper.SQL_DELETE_MERCHANTS_TABLE);
        onCreate(db);
    }
}
