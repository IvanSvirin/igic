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

public class CharityAccountRequest {
    private Context context;

    public CharityAccountRequest(Context context) {
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
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/account");
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
                    values.put(DataContract.CharityAccounts.COLUMN_TOTAL_PAID_AMOUNT, jObj.getDouble("total_paid_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_NEXT_CHECK_AMOUNT, jObj.getDouble("next_check_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_CAUSE_DASHBOARD_URL, jObj.getString("cause_dashboard_url"));
                    values.put(DataContract.CharityAccounts.COLUMN_LAST_NAME, jObj.getString("last_name"));
                    String email = jObj.getString("email");
                    Utilities.saveEmail(context, email);
                    values.put(DataContract.CashbackAccounts.COLUMN_EMAIL, email);
                    values.put(DataContract.CharityAccounts.COLUMN_FIRST_NAME, jObj.getString("first_name"));
                    values.put(DataContract.CharityAccounts.COLUMN_TOTAL_RAISED, jObj.getDouble("total_raised"));
                    values.put(DataContract.CharityAccounts.COLUMN_EARNED_TOTAL, jObj.getDouble("earned_total"));
                    values.put(DataContract.CharityAccounts.COLUMN_SELECT_CAUSE_URL, jObj.getString("select_cause_url"));
                    values.put(DataContract.CharityAccounts.COLUMN_MEMBER_SETTINGS_URL, jObj.getString("member_settings_url"));
                    values.put(DataContract.CharityAccounts.COLUMN_PENDING_AMOUNT, jObj.getDouble("pending_amount"));
                    values.put(DataContract.CharityAccounts.COLUMN_MEMBER_DATE, jObj.getString("member_date"));
                    values.put(DataContract.CharityAccounts.COLUMN_REFERRER_ID, jObj.getString("referrer_id"));

                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startInsert(DataInsertHandler.ACCOUNT_TOKEN, null, DataContract.URI_CHARITY_ACCOUNT, values);
                    EventBus.getDefault().post(new AccountEvent(true, null));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}

