package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.AccountEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import db.DataContract;
import de.greenrobot.event.EventBus;

public class CashBackAccountRequest {
    private Context context;

    public CashBackAccountRequest(Context context) {
        this.context = context;
    }

    public void fetchData() {
        new CharityAccountRequestTask().execute();
    }

    private class CharityAccountRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://app.iconsumer.com/rest/icon/api/v1/account");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                inputStream.close();
                urlConnection.disconnect();
                jsonString = sb.toString();
                while (jsonString.charAt(0) != '{' && jsonString.charAt(0) != '[') {
                    jsonString = jsonString.substring(1);
                }
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
            try {
                jObj = new JSONObject(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (jObj != null) {
                    ContentValues values = new ContentValues();
                    values.put(DataContract.CashbackAccounts.COLUMN_CASH_PENDING_AMOUNT, jObj.getDouble("cash_pending_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_PAYMENTS_TOTAL_AMOUNT, jObj.getDouble("payments_total_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_NEXT_CHECK_AMOUNT, jObj.getDouble("next_check_amount"));
                    values.put(DataContract.CashbackAccounts.COLUMN_TOTAL_EARNED, jObj.getDouble("total_earned"));
                    values.put(DataContract.CashbackAccounts.COLUMN_LAST_NAME, jObj.getString("last_name"));
                    String email = jObj.getString("email");
                    Utilities.saveEmail(context, email);
                    values.put(DataContract.CashbackAccounts.COLUMN_EMAIL, email);
                    values.put(DataContract.CashbackAccounts.COLUMN_FIRST_NAME, jObj.getString("first_name"));
                    values.put(DataContract.CashbackAccounts.COLUMN_MEMBER_DATE, jObj.getString("member_date"));
                    values.put(DataContract.CashbackAccounts.COLUMN_NEXT_PAYMENT_DATE, jObj.getString("next_payment_date"));
                    values.put(DataContract.CashbackAccounts.COLUMN_REFERRER_ID, jObj.getString("referrer_id"));

                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startInsert(DataInsertHandler.ACCOUNT_TOKEN, null, DataContract.URI_CASH_BACK_ACCOUNT, values);
                    EventBus.getDefault().post(new AccountEvent(true, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

