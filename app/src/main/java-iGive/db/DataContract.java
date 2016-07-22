package db;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class DataContract {
    public final static String CONTENT_AUTHORITY = "com.cashback.provider";
    public final static String BASE_CONTENT = "content://";
    public final static Uri BASE_CONTENT_URI = Uri.parse(BASE_CONTENT + CONTENT_AUTHORITY);

    public final static Uri URI_MERCHANTS = Uri.withAppendedPath(BASE_CONTENT_URI, "merchants");
    public final static Uri URI_COUPONS = Uri.withAppendedPath(BASE_CONTENT_URI, "coupons");
    public final static Uri URI_FAVORITES = Uri.withAppendedPath(BASE_CONTENT_URI, "favorites");
    public final static Uri URI_EXTRAS = Uri.withAppendedPath(BASE_CONTENT_URI, "extras");

    public final static Uri URI_CATEGORIES = Uri.withAppendedPath(BASE_CONTENT_URI, "categories");
    public final static Uri URI_PAYMENTS = Uri.withAppendedPath(BASE_CONTENT_URI, "payments");
    public final static Uri URI_SHOPPING_TRIPS = Uri.withAppendedPath(BASE_CONTENT_URI, "shopping_trips");
    public static final Uri URI_ORDERS = Uri.withAppendedPath(BASE_CONTENT_URI, "orders");
    public static final Uri URI_CHARITY_ORDERS = Uri.withAppendedPath(BASE_CONTENT_URI, "charity_orders");
    public static final Uri URI_CHARITY_ACCOUNT = Uri.withAppendedPath(BASE_CONTENT_URI, "charity_accounts");
    public static final Uri URI_CASH_BACK_ACCOUNT = Uri.withAppendedPath(BASE_CONTENT_URI, "cashback_accounts");
    public static final Uri URI_MISC = Uri.withAppendedPath(BASE_CONTENT_URI, "misc");


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
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_OWNERS_BENEFIT = "owners_benefit";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "merchants";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "merchants";
    }

    public static class Favorites implements BaseColumns {
        public static final String TABLE_NAME = "favorites";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMMISSION = "commission";
        public static final String COLUMN_EXCEPTION_INFO = "exception_info";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_GIFT_CARD = "gift_card";
        public static final String COLUMN_AFFILIATE_URL = "affiliate_url";
        public static final String COLUMN_LOGO_URL = "logo_url";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_OWNERS_BENEFIT = "owners_benefit";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "favorites";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "favorites";
    }

    public static class Extras implements BaseColumns {
        public static final String TABLE_NAME = "extras";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_COMMISSION = "commission";
        public static final String COLUMN_EXCEPTION_INFO = "exception_info";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_GIFT_CARD = "gift_card";
        public static final String COLUMN_AFFILIATE_URL = "affiliate_url";
        public static final String COLUMN_LOGO_URL = "logo_url";
        public static final String COLUMN_IS_FAVORITE = "is_favorite";
        public static final String COLUMN_COMMISSION_WAS = "commission_was";
        public static final String COLUMN_OWNERS_BENEFIT = "owners_benefit";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "extras";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "extras";
    }

    public static class Coupons implements BaseColumns {
        public static final String TABLE_NAME = "coupons";
        public static final String COLUMN_COUPON_ID = "coupon_id";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_COUPON_TYPE = "coupon_type";
        public static final String COLUMN_RESTRICTIONS = "restrictions";
        public static final String COLUMN_LABEL = "label";
        public static final String COLUMN_COUPON_CODE = "coupon_code";
        public static final String COLUMN_EXPIRATION_DATE = "expiration_date";
        public static final String COLUMN_AFFILIATE_URL = "affiliate_url";
        public static final String COLUMN_VENDOR_LOGO_URL = "vendor_logo_url";
        public static final String COLUMN_VENDOR_COMMISSION = "vendor_commission";
        public static final String COLUMN_OWNERS_BENEFIT = "owners_benefit";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "coupons";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "coupons";
    }

    public static class Categories implements BaseColumns {
        public static final String TABLE_NAME = "categories";
        public static final String COLUMN_CATEGORY_ID = "category_id";
        public static final String COLUMN_NAME = "name";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "categories";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "categories";
    }

    public static class Payments implements BaseColumns {
        public static final String TABLE_NAME = "payments";
        public static final String _ID = "_id";
        public static final String COLUMN_PAYMENT_DATE = "payment_date";
        public static final String COLUMN_PAYMENT_AMOUNT = "payment_amount";
        public static final String COLUMN_CLEARED = "cleared";
        public static final String COLUMN_CHECK_NUMBER = "check_number";
        public static final String COLUMN_SEND_TO = "send_to";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "payments";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "payments";
    }

    public static class ShoppingTrips implements BaseColumns {
        public static final String TABLE_NAME = "shopping_trips";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_CONFIRMATION_NUMBER = "confirmation_number";
        public static final String COLUMN_TRIP_DATE = "trip_date";
        public static final String COLUMN_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_VENDOR_LOGO_URL = "vendor_logo_url";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "shopping_trips";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "shopping_trips";
    }

    public static class CashBackOrders implements BaseColumns {
        public static final String _ID = "_id";
        public static final String TABLE_NAME = "orders";
        public static final String COLUMN_ORDER_ID = "order_id";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String COLUMN_PURCHASE_TOTAL = "purchase_total";
        public static final String COLUMN_CONFIRMATION_NUMBER = "confirmation_number";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_POSTED_DATE = "posted_date";
        public static final String COLUMN_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_VENDOR_LOGO_URL = "vendor_logo_url";
        public static final String COLUMN_SHARED_STOCK_AMOUNT = "shared_stock_amount";
        public static final String COLUMN_CASH_BACK = "cash_back";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "orders";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "orders";
    }

    public static class CharityOrders implements BaseColumns {
        public static final String TABLE_NAME = "charity_orders";
        public static final String COLUMN_VENDOR_ID = "vendor_id";
        public static final String _ID = "_id";
        public static final String COLUMN_ORDER_ID = "order_id";
        public static final String COLUMN_PURCHASE_TOTAL = "purchase_total";
        public static final String COLUMN_CONFIRMATION_NUMBER = "confirmation_number";
        public static final String COLUMN_ORDER_DATE = "order_date";
        public static final String COLUMN_POSTED_DATE = "posted_date";
        public static final String COLUMN_VENDOR_NAME = "vendor_name";
        public static final String COLUMN_VENDOR_LOGO_URL = "vendor_logo_url";
        public static final String COLUMN_CAUSE_NAME = "cause_name";
        public static final String COLUMN_CAUSE_LOGO_URL = "cause_logo_url";
        public static final String COLUMN_AMOUNT_DONATED = "amount_donated";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "charity_orders";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "charity_orders";
    }

    public static class CharityAccounts implements BaseColumns {
        public static final String TABLE_NAME = "charity_accounts";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_MEMBER_DATE = "member_date";
        public static final String COLUMN_NEXT_CHECK_AMOUNT = "next_check_amount";
        public static final String COLUMN_PENDING_AMOUNT = "pending_amount";
        public static final String COLUMN_TOTAL_PAID_AMOUNT = "total_paid_amount";
        public static final String COLUMN_TOTAL_PAID_DATE = "total_paid_date";
        public static final String COLUMN_TOTAL_RAISED = "total_raised";
        public static final String COLUMN_TOTAL_EARNED = "total_earned";
        public static final String COLUMN_CAUSE_DASHBOARD_URL = "cause_dashboard_url";
        public static final String COLUMN_SELECT_CAUSE_URL = "select_cause_url";
        public static final String COLUMN_REFERRER_ID = "referrer_id";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "charity_accounts";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "charity_accounts";
    }

    public static class CashbackAccounts implements BaseColumns {
        public static final String TABLE_NAME = "cashback_accounts";
        public static final String COLUMN_TOKEN = "token";
        public static final String COLUMN_FIRST_NAME = "first_name";
        public static final String COLUMN_LAST_NAME = "last_name";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_NEXT_PAYMENT_DATE = "next_payment_date";
        public static final String COLUMN_MEMBER_DATE = "member_date";
        public static final String COLUMN_CASH_PENDING_AMOUNT = "cash_pending_amount";
        public static final String COLUMN_PAYMENTS_TOTAL_AMOUNT = "payments_total_amount";
        public static final String COLUMN_NEXT_CHECK_AMOUNT = "next_check_amount";
        public static final String COLUMN_REFERRER_ID = "referrer_id";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "cashback_accounts";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "cashback_accounts";
    }

    public static class Misc implements BaseColumns {
        public static final String TABLE_NAME = "misc";
        public static final String COLUMN_SHARE_DEAL_TEXT = "share_deal_text";
        public static final String COLUMN_TELL_A_FRIEND_TEXT = "tell_a_friend_text";
        public static final String COLUMN_TELL_A_FRIEND_BANNER_URL = "tell_a_friend_banner_url";

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "misc";
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + "misc";
    }
}
