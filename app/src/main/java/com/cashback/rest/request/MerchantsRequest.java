package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.cashback.R;
import com.cashback.Utilities;
import db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.MerchantsEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class MerchantsRequest {
    private Context context;

    public MerchantsRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new MerchantsRequestTask().execute();
    }

    private class MerchantsRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.merchants_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                EventBus.getDefault().post(new MerchantsEvent(false, "No stores data"));
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
                EventBus.getDefault().post(new MerchantsEvent(false, "No stores data"));
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                EventBus.getDefault().post(new MerchantsEvent(false, "No stores data"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jsonArray != null) {
                List<ContentValues> listValues = new ArrayList<>(jsonArray.length());
                ContentValues values;
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        values = new ContentValues();
                        values.put(DataContract.Merchants.COLUMN_COMMISSION, jObj.getDouble("commission"));
                        values.put(DataContract.Merchants.COLUMN_AFFILIATE_URL, jObj.getString("affiliate_url"));
                        values.put(DataContract.Merchants.COLUMN_IS_FAVORITE, jObj.getInt("is_favorite"));
                        values.put(DataContract.Merchants.COLUMN_DESCRIPTION, jObj.getString("description"));
                        values.put(DataContract.Merchants.COLUMN_LOGO_URL, jObj.getString("logo_url"));
                        values.put(DataContract.Merchants.COLUMN_GIFT_CARD, jObj.getInt("gift_card"));
                        values.put(DataContract.Merchants.COLUMN_EXCEPTION_INFO, jObj.getString("exception_info"));
                        values.put(DataContract.Merchants.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                        values.put(DataContract.Merchants.COLUMN_NAME, jObj.getString("name"));
                        if (jObj.has("owners_benefit")) {
                            if (jObj.getBoolean("owners_benefit")) {
                                values.put(DataContract.Merchants.COLUMN_OWNERS_BENEFIT, 1);
                            }
                        } else {
                            values.put(DataContract.Merchants.COLUMN_OWNERS_BENEFIT, 0);
                        }
                        listValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.MERCHANTS_TOKEN, false, DataContract.URI_MERCHANTS, listValues.toArray(new ContentValues[listValues.size()]));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new MerchantsEvent(false, "No stores data"));
                }
            }
        }
    }
}
