package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.ShoppingTripsEvent;

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

public class ShoppingTripsRequest {
    private Context context;

    public ShoppingTripsRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new ShoppingTripsRequestTask().execute();
    }

    private class ShoppingTripsRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.trip_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new ShoppingTripsEvent(false, "No visits data"));
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
                EventBus.getDefault().post(new ShoppingTripsEvent(false, "No visits data"));
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
                EventBus.getDefault().post(new ShoppingTripsEvent(false, "No visits data"));
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
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_NAME, jObj.getString("vendor_name"));
                        values.put(DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER, jObj.getInt("confirmation_number"));
                        values.put(DataContract.ShoppingTrips.COLUMN_TRIP_DATE, jObj.getString("trip_date"));
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_LOGO_URL, jObj.getString("vendor_logo_url"));
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                        listValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.SHOPPING_TRIPS_TOKEN, false, DataContract.URI_SHOPPING_TRIPS, listValues.toArray(new ContentValues[listValues.size()]));
                    EventBus.getDefault().post(new ShoppingTripsEvent(true, "OK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new ShoppingTripsEvent(false, "No visits data"));
                }
            } else {
                EventBus.getDefault().post(new ShoppingTripsEvent(false, "No visits data"));
            }
        }
    }
}
