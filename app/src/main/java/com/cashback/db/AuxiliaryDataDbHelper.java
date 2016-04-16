package com.cashback.db;

/**
 * Created by ivansv on 16.04.2016.
 */
public class AuxiliaryDataDbHelper {
    private static final String INT_TYPE = " INTEGER";
    private static final String BOOL_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL"; // approximately how double
    private static final String DATE_TYPE = " TEXT";
    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String NULL_TYPE = " NULL";
    private static final String PRIMARY_TYPE = " INTEGER PRIMARY KEY,";
    private static final String DELETE_BEHAVIOR = " ON DELETE CASCADE";
    private static final String DELIMITER = ",";

    public final String SQL_CREATE_MERCHANTS_TABLE =
            "CREATE TABLE " + DataContract.Merchants.TABLE_NAME + "(" +
                    DataContract.Merchants._ID + PRIMARY_TYPE +
                    DataContract.Merchants.COLUMN_VENDOR_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.Merchants.COLUMN_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_COMMISSION + REAL_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_EXCEPTION_INFO + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_DESCRIPTION + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_GIFT_CARD + BOOL_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_LOGO_URL + TEXT_TYPE + DELIMITER +
                    " )";
    public final String SQL_DELETE_MERCHANTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Merchants.TABLE_NAME;
}
