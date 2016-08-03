package com.cashback.db;

import db.DataContract;

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
                    DataContract.Merchants.COLUMN_IS_FAVORITE + INT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_OWNERS_BENEFIT + BOOL_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_MERCHANTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Merchants.TABLE_NAME;

    public final String SQL_CREATE_FAVORITES_TABLE =
            "CREATE TABLE " + DataContract.Favorites.TABLE_NAME + "(" +
                    DataContract.Favorites._ID + PRIMARY_TYPE +
                    DataContract.Favorites.COLUMN_VENDOR_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.Favorites.COLUMN_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_COMMISSION + REAL_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_EXCEPTION_INFO + TEXT_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_DESCRIPTION + TEXT_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_GIFT_CARD + BOOL_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_IS_FAVORITE + INT_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_OWNERS_BENEFIT + BOOL_TYPE + DELIMITER +
                    DataContract.Favorites.COLUMN_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_FAVORITES_TABLE = "DROP TABLE IF EXISTS " + DataContract.Merchants.TABLE_NAME;

    public final String SQL_CREATE_EXTRAS_TABLE =
            "CREATE TABLE " + DataContract.Extras.TABLE_NAME + "(" +
                    DataContract.Extras._ID + PRIMARY_TYPE +
                    DataContract.Extras.COLUMN_VENDOR_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.Extras.COLUMN_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_COMMISSION + REAL_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_EXCEPTION_INFO + TEXT_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_DESCRIPTION + TEXT_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_GIFT_CARD + BOOL_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_IS_FAVORITE + INT_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_COMMISSION_WAS + TEXT_TYPE + DELIMITER +
                    DataContract.Merchants.COLUMN_OWNERS_BENEFIT + BOOL_TYPE + DELIMITER +
                    DataContract.Extras.COLUMN_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_EXTRAS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Extras.TABLE_NAME;

    public final String SQL_CREATE_HOT_DEALS_TABLE =
            "CREATE TABLE " + DataContract.HotDeals.TABLE_NAME + "(" +
                    DataContract.HotDeals._ID + PRIMARY_TYPE +
                    DataContract.HotDeals.COLUMN_COUPON_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.HotDeals.COLUMN_VENDOR_ID + INT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_COUPON_TYPE + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_RESTRICTIONS + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_COUPON_CODE + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_LABEL + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_EXPIRATION_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_OWNERS_BENEFIT + BOOL_TYPE + DELIMITER +
                    DataContract.HotDeals.COLUMN_VENDOR_COMMISSION + REAL_TYPE +
                    " )";
    public final String SQL_DELETE_HOT_DEALS_TABLE = "DROP TABLE IF EXISTS " + DataContract.HotDeals.TABLE_NAME;

    public final String SQL_CREATE_COUPONS_TABLE =
            "CREATE TABLE " + DataContract.Coupons.TABLE_NAME + "(" +
                    DataContract.Coupons._ID + PRIMARY_TYPE +
                    DataContract.Coupons.COLUMN_COUPON_ID + INT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.Coupons.COLUMN_VENDOR_ID + INT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_COUPON_TYPE + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_RESTRICTIONS + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_COUPON_CODE + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_LABEL + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_EXPIRATION_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_AFFILIATE_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_OWNERS_BENEFIT + BOOL_TYPE + DELIMITER +
                    DataContract.Coupons.COLUMN_VENDOR_COMMISSION + REAL_TYPE +
                    " )";
    public final String SQL_DELETE_COUPONS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Coupons.TABLE_NAME;

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
                    DataContract.Payments.COLUMN_CLEARED + TEXT_TYPE + DELIMITER +
                    DataContract.Payments.COLUMN_CHECK_NUMBER + REAL_TYPE + DELIMITER +
                    DataContract.Payments.COLUMN_SEND_TO + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_PAYMENTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.Payments.TABLE_NAME;

    public final String SQL_CREATE_SHOPPING_TRIPS_TABLE =
            "CREATE TABLE " + DataContract.ShoppingTrips.TABLE_NAME + "(" +
                    DataContract.ShoppingTrips._ID + PRIMARY_TYPE +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_ID + INT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER + INT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_TRIP_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.ShoppingTrips.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_SHOPPING_TRIPS_TABLE = "DROP TABLE IF EXISTS " + DataContract.ShoppingTrips.TABLE_NAME;

    public final String SQL_CREATE_ORDERS_TABLE =
            "CREATE TABLE " + DataContract.CashBackOrders.TABLE_NAME + "(" +
                    DataContract.CashBackOrders._ID + PRIMARY_TYPE +
                    DataContract.CashBackOrders.COLUMN_VENDOR_ID + INT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_PURCHASE_TOTAL + REAL_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_CONFIRMATION_NUMBER + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_ORDER_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_ORDER_ID + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_POSTED_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_VENDOR_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_SHARED_STOCK_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CashBackOrders.COLUMN_CASH_BACK + REAL_TYPE +
                    " )";
    public final String SQL_DELETE_ORDERS_TABLE = "DROP TABLE IF EXISTS " + DataContract.CashBackOrders.TABLE_NAME;

    public final String SQL_CREATE_CHARITY_ORDERS_TABLE =
            "CREATE TABLE " + DataContract.CharityOrders.TABLE_NAME + "(" +
                    DataContract.CharityOrders._ID + PRIMARY_TYPE +
                    DataContract.CharityOrders.COLUMN_VENDOR_ID + INT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_PURCHASE_TOTAL + REAL_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_CONFIRMATION_NUMBER + INT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_ORDER_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_ORDER_ID + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_POSTED_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_VENDOR_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_VENDOR_LOGO_URL + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_CAUSE_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_CAUSE_LOGO_URL + TEXT_TYPE + DELIMITER +
                    DataContract.CharityOrders.COLUMN_AMOUNT_DONATED + REAL_TYPE +
                    " )";
    public final String SQL_DELETE_CHARITY_ORDERS_TABLE = "DROP TABLE IF EXISTS " + DataContract.CharityOrders.TABLE_NAME;

    public final String SQL_CREATE_CHARITY_ACCOUNTS_TABLE =
            "CREATE TABLE " + DataContract.CharityAccounts.TABLE_NAME + "(" +
                    DataContract.CharityAccounts._ID + PRIMARY_TYPE +
                    DataContract.CharityAccounts.COLUMN_TOKEN + TEXT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_FIRST_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_LAST_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_EMAIL + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_MEMBER_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_NEXT_CHECK_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_TOTAL_RAISED + REAL_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_CAUSE_DASHBOARD_URL + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_MEMBER_SETTINGS_URL + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_REFERRER_ID + TEXT_TYPE + DELIMITER +
                    DataContract.CharityAccounts.COLUMN_SELECT_CAUSE_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_CHARITY_ACCOUNTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.CharityAccounts.TABLE_NAME;

    public final String SQL_CREATE_CASHBACK_ACCOUNTS_TABLE =
            "CREATE TABLE " + DataContract.CashbackAccounts.TABLE_NAME + "(" +
                    DataContract.CashbackAccounts._ID + PRIMARY_TYPE +
                    DataContract.CashbackAccounts.COLUMN_TOKEN + TEXT_TYPE + " UNIQUE" + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_FIRST_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_LAST_NAME + TEXT_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_EMAIL + TEXT_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_MEMBER_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_TOTAL_EARNED + REAL_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_NEXT_PAYMENT_DATE + TEXT_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_CASH_PENDING_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_PAYMENTS_TOTAL_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_NEXT_CHECK_AMOUNT + REAL_TYPE + DELIMITER +
                    DataContract.CashbackAccounts.COLUMN_REFERRER_ID + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_CASHBACK_ACCOUNTS_TABLE = "DROP TABLE IF EXISTS " + DataContract.CashbackAccounts.TABLE_NAME;

    public final String SQL_CREATE_MISC_TABLE =
            "CREATE TABLE " + DataContract.Misc.TABLE_NAME + "(" +
                    DataContract.Misc._ID + PRIMARY_TYPE +
                    DataContract.Misc.COLUMN_SHARE_DEAL_TEXT + TEXT_TYPE + DELIMITER +
                    DataContract.Misc.COLUMN_TELL_A_FRIEND_TEXT + TEXT_TYPE + DELIMITER +
                    DataContract.Misc.COLUMN_TELL_A_FRIEND_BANNER_URL + TEXT_TYPE +
                    " )";
    public final String SQL_DELETE_MISC_TABLE = "DROP TABLE IF EXISTS " + DataContract.Misc.TABLE_NAME;
}