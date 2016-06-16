package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.IMerchants;
import com.cashback.rest.event.ExtrasEvent;
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

public class ExtrasRequest {
    private Context context;

    public ExtrasRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new ExtrasRequestTask().execute();
    }

    private class ExtrasRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.extras_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
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
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            List<ContentValues> listValues = new ArrayList<>(jsonArray.length());
            ContentValues values;
            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    jObj = jsonArray.getJSONObject(i);
                    values = new ContentValues();
                    values.put(DataContract.Extras.COLUMN_COMMISSION, jObj.getDouble("commission"));
                    values.put(DataContract.Extras.COLUMN_AFFILIATE_URL, jObj.getString("affiliate_url"));
                    values.put(DataContract.Extras.COLUMN_IS_FAVORITE, jObj.getInt("is_favorite"));
                    values.put(DataContract.Extras.COLUMN_DESCRIPTION, jObj.getString("description"));
                    values.put(DataContract.Extras.COLUMN_LOGO_URL, jObj.getString("logo_url"));
                    values.put(DataContract.Extras.COLUMN_GIFT_CARD, jObj.getInt("gift_card"));
                    values.put(DataContract.Extras.COLUMN_EXCEPTION_INFO, jObj.getString("exception_info"));
                    values.put(DataContract.Extras.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                    values.put(DataContract.Extras.COLUMN_NAME, jObj.getString("name"));
                    values.put(DataContract.Extras.COLUMN_COMMISSION_WAS, jObj.getString("commission_was"));
                    listValues.add(values);
                }
                DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                handler.startBulkInsert(DataInsertHandler.EXTRAS_TOKEN, false, DataContract.URI_EXTRAS, listValues.toArray(new ContentValues[listValues.size()]));
            } catch (JSONException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new ExtrasEvent(false, "No merchants featured data"));
            }
        }

    }
}
