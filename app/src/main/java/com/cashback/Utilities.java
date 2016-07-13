package com.cashback;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import com.cashback.ui.login.LoginActivity;

public class Utilities {
    private final static String PREF_TOKEN_KEY = "pref_token";
    private static final String PREF_ENTRY_KEY = "pref_entry";
    private static final String PREF_IDFA_KEY = "pref_idfa";
    private static final String PREF_EMAIL_KEY = "pref_email";
    private static final String PREF_SHARE_DEAL_TEXT_KEY = "pref_share_deal_text";
    private static final String PREF_TELL_A_FRIEND_TEXT_KEY = "pref_tell_a_friend_text";

    public static boolean isLoggedIn(Context context) {
        return !TextUtils.isEmpty(retrieveUserToken(context)) && retrieveUserEntry(context);
    }

    public static boolean saveIdfa(Context context, String idfa) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_IDFA_KEY, idfa);
        return editor.commit();
    }

    public static String retrieveIdfa(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_IDFA_KEY, null);
    }

    public static boolean saveEmail(Context context, String email) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_EMAIL_KEY, email);
        return editor.commit();
    }

    public static String retrieveEmail(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_EMAIL_KEY, null);
    }

    public static boolean saveShareDealText(Context context, String s) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_SHARE_DEAL_TEXT_KEY, s);
        return editor.commit();
    }

    public static String retrieveShareDealText(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_SHARE_DEAL_TEXT_KEY, null);
    }

    public static boolean saveTellFriendText(Context context, String s) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TELL_A_FRIEND_TEXT_KEY, s);
        return editor.commit();
    }

    public static String retrieveTellFriendText(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(PREF_TELL_A_FRIEND_TEXT_KEY, null);
    }

    public static boolean saveUserEntry(Context context, boolean entry) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(PREF_ENTRY_KEY, entry);
        return editor.commit();
    }

    public static boolean retrieveUserEntry(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(PREF_ENTRY_KEY, false);
    }


    public static boolean saveUserToken(Context context, String token) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PREF_TOKEN_KEY, token);
        return editor.commit();
    }

    public static String retrieveUserToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = pref.getString(PREF_TOKEN_KEY, null);
        return token;
    }

    public static boolean removeUserToken(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(PREF_TOKEN_KEY);
        return editor.commit();
    }


    public static void setStateSendingTokenToServer(Context context, final boolean state) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("sentTokenToServer", state);
        editor.apply();
    }

    public static boolean isActiveConnection(Context context) {
        ConnectivityManager mng = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo net = mng.getActiveNetworkInfo();
        if (net != null && net.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    public static boolean isCashBackNotifyOn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.cashback_notify), false);
    }

    public static void setCashBackNotify(Context context, boolean set) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.cashback_notify), set);
        editor.apply();
    }

    public static boolean isDealsNotifyOn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.deals_notify), false);
    }

    public static void setDealsNotify(Context context, boolean set) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.deals_notify), set);
        editor.apply();
    }

    public static boolean isPaymentsNotifyOn(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.payment_notify), false);
    }

    public static void setPaymentsNotify(Context context, boolean set) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.payment_notify), set);
        editor.apply();
    }

    public static boolean isShowTour(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_show_tour_key), true);
    }

    public static void setShowTour(Context context, final boolean toShow) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(context.getString(R.string.pref_show_tour_key), toShow);
        editor.apply();
    }

    public static String getMonth(String s) {
        switch (s) {
            case "01":
                return "Jan";
            case "02":
                return "Feb";
            case "03":
                return "Mar";
            case "04":
                return "Apr";
            case "05":
                return "May";
            case "06":
                return "Jun";
            case "07":
                return "Jul";
            case "08":
                return "Aug";
            case "09":
                return "Sep";
            case "10":
                return "Oct";
            case "11":
                return "Nov";
            case "12":
                return "Dec";
        }
        return "";
    }

    public static String getFullMonth(String s) {
        switch (s) {
            case "01":
                return "JANUARY";
            case "02":
                return "FEBRUARY";
            case "03":
                return "MARCH";
            case "04":
                return "APRIL";
            case "05":
                return "MAY";
            case "06":
                return "JUNE";
            case "07":
                return "JULY";
            case "08":
                return "AUGUST";
            case "09":
                return "SEPTEMBER";
            case "10":
                return "OCTOBER";
            case "11":
                return "NOVEMBER";
            case "12":
                return "DECEMBER";
        }
        return "";
    }

    public static String decToHex(int decimal) {
        String heximal = Integer.toHexString(decimal);
        return heximal;
    }

    public static int hexToDec(String inputHexNumber) {
        String hexDigits = "0123456789ABCDEF";
        inputHexNumber = inputHexNumber.toUpperCase();
        int result = 0;
        for (int i = 0; i < inputHexNumber.length(); i++) {
            char c = inputHexNumber.charAt(i);
            int hexDigit = hexDigits.indexOf(c);
            result += 16 * result + hexDigit;
        }
        return result;
    }

    public static ProgressDialog onCreateProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Please, wait...");
        return progressDialog;
    }

    public static void showFailNotification(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setTitle("Failure")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void needLoginDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.please_log_in)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public static void needLoginForFavoritesDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.please_log_in_favorites)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    public static String replaceSpace(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ' ') {
                s = s.substring(0, i) + "%20" + s.substring(i + 1, s.length());
            }
        }
        return s;
    }
}
