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
                    DataContract.Merchants.COLUMN_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_MERCHANTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Merchants.TABLE_NAME;

    // TODO: 4/19/2016 TEST - will be deleted
    public final String SQL_CREATE_COUPONS_TABLE =
            "CREATE TABLE " + DataContract.OfferEntry.TABLE_NAME + "(" +
                    DataContract.OfferEntry._ID + PRIMARY_TYPE +
                    DataContract.OfferEntry.COLUMN_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.OfferEntry.COLUMN_MSG + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_DESCRIPTION + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_CODE + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_EXPIRE + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_EXPIRE_RAW + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_LOGO + TEXT_TYPE + DELIMITER +
                    DataContract.OfferEntry.COLUMN_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_COUPONS_TABLE = "DROP TABLE IF EXISTS " + DataContract.OfferEntry.TABLE_NAME;


//    public final String SQL_CREATE_COUPONS_TABLE =
//            "CREATE TABLE " + DataContract.Coupons.TABLE_NAME + "(" +
//                    DataContract.Coupons._ID + PRIMARY_TYPE +
//                    DataContract.Coupons.COLUMN_COUPON_ID + INT_TYPE + " UNIQUE" + DELIMITER +
//                    DataContract.Coupons.COLUMN_VENDOR_ID + INT_TYPE + " UNIQUE" + DELIMITER +
//                    DataContract.Coupons.COLUMN_COUPON_TYPE + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_RESTRICTIONS + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_COUPON_CODE + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_EXPIRATION_DATE + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE + DELIMITER +
//                    DataContract.Coupons.COLUMN_VENDOR_COMMISSION + REAL_TYPE + DELIMITER +
//                    " )";
//    public final String SQL_DELETE_COUPONS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Coupons.TABLE_NAME;

    public final String SQL_CREATE_CATEGORIES_TABLE =
            "CREATE TABLE " + DataContract.Categories.TABLE_NAME + "(" +
                    DataContract.Categories._ID + PRIMARY_TYPE +
                    DataContract.Categories.COLUMN_CATEGORY_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.Categories.COLUMN_NAME + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_CATEGORIES_TABLE = "DROP TABLE IF EXISTS " + DataContract.Categories.TABLE_NAME;

    public final String SQL_CREATE_PAYMENTS_TABLE =
            "CREATE TABLE " + DataContract.Payments.TABLE_NAME + "(" +
                    DataContract.Payments._ID + PRIMARY_TYPE +
                    DataContract.Payments.COLUMN_PAYMENT_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.Payments.COLUMN_PAYMENT_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.Payments.COLUMN_PAYMENT_ACCOUNT + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_PAYMENTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Payments.TABLE_NAME;

    public final String SQL_CREATE_SHOPPING_TRIPS_TABLE =
            "CREATE TABLE " + DataContract.ShoppingTrips.TABLE_NAME + "(" +
                    DataContract.ShoppingTrips._ID + PRIMARY_TYPE +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER + INT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_TRIP_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_SHOPPING_TRIPS_TABLE = "DROP TABLE IF EXISTS " + DataContract.ShoppingTrips.TABLE_NAME;
}
