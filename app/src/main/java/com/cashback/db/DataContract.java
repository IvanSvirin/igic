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
    // TODO: 4/19/2016 TEST - will be deleted
    public final static Uri URI_COUPONS = Uri.withAppendedPath(BASE_CONTENT_URI, "offer_coupons");
//    public final static Uri URI_COUPONS = Uri.withAppendedPath(BASE_CONTENT_URI, "coupons");
    public final static Uri URI_FAVORITES = Uri.withAppendedPath(BASE_CONTENT_URI, "favorites");
    public final static Uri URI_EXTRAS = Uri.withAppendedPath(BASE_CONTENT_URI, "extras");

    public final static Uri URI_IMAGES = Uri.withAppendedPath(BASE_CONTENT_URI, "images");




    public static class Merchants implements BaseColumns {
        public static final String TABLE_NAME = "merchants";
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

    public static class Coupons implements BaseColumns {
        public static final String TABLE_NAME = "coupons";
        public static final String COLUMN_COUPON_ID = "coupon_id";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_COUPON_TYPE = "coupon_type";
        public static final String COLUMN_RESTRICTIONS = "restrictions";
        public static final String COLUMN_COUPON_CODE = "coupon_code";
        public static final String COLUMN_EXPIRATION_DATE = "expiration_date";
        public static final String COLUMN_AFFILIATE_URL = "affiliate_url";
        public static final String COLUMN_VENDOR_LOGO_URL = "vendor_logo_url";
        public static final String COLUMN_VENDOR_COMMISSION = "vendor_commission";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "coupons";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "coupons";
    }
    // TODO: 4/19/2016 TEST - will be deleted
    public static class OfferEntry implements BaseColumns {
        public static final String TABLE_NAME = "offer_coupons";
        public static final String COLUMN_ID = "id_offer";
        public static final String COLUMN_MSG = "pay_out_msg";
        public static final String COLUMN_DESCRIPTION = "desc";
        public static final String COLUMN_CODE = "code";
        public static final String COLUMN_EXPIRE = "expire";
        public static final String COLUMN_EXPIRE_RAW = "expire_iso";
        public static final String COLUMN_LOGO = "logo";
        public static final String COLUMN_URL = "url";
        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "offer_coupons";
    }

}
