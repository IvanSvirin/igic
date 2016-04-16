package com.cashback.db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by I.Svirin on 4/12/2016.
 */
public class DataContract {
    public final static String CONTENT_AUTHORITY = "com.cashback.provider";
    public final static String BASE_CONTENT = "content://";
    public final static Uri BASE_CONTENT_URI = Uri.parse(BASE_CONTENT + CONTENT_AUTHORITY);

    public final static Uri URI_MERCHANTS = Uri.withAppendedPath(BASE_CONTENT_URI, "merchants");
    public final static Uri URI_FAVORITE_MERCHANTS = Uri.withAppendedPath(BASE_CONTENT_URI, "favorite_merchants");
    public final static Uri URI_EXTRA_MERCHANTS = Uri.withAppendedPath(BASE_CONTENT_URI, "extra_merchants");



    public static class Merchants implements BaseColumns {
        public static final String TABLE_NAME = "hot_deals";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMMISSION = "commission";
        public static final String COLUMN_EXCEPTION_INFO = "exception_info";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_GIFT_CARD = "gift_card";
        public static final String COLUMN_AFFILIATE_URL = "affiliate_url";
        public static final String COLUMN_LOGO_URL = "logo_url";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "merchants";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "merchants";
    }
}
