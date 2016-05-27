package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.event.ExtrasEvent;
import com.cashback.rest.event.OrdersEvent;

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

/**
 * Created by I.Svirin on 5/27/2016.
 */
public class CharityOrdersRequest {
    private Context context;

    public CharityOrdersRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        new CharityOrdersRequestTask().execute();
    }

    private class CharityOrdersRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/account/history/order/");
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
                    values.put(DataContract.CharityOrders.COLUMN_VENDOR_NAME, jObj.getString("vendor_name"));
                    // TODO: 5/27/2016 not forever
                    values.put(DataContract.CharityOrders.COLUMN_CONFIRMATION_NUMBER, jObj.getString("confirmation_number"));
//                    values.put(DataContract.CharityOrders.COLUMN_CONFIRMATION_NUMBER, jObj.getInt("confirmation_number"));
                    values.put(DataContract.CharityOrders.COLUMN_AMOUNT_DONATED, jObj.getDouble("amount_donated"));
                    values.put(DataContract.CharityOrders.COLUMN_ORDER_DATE, jObj.getString("order_date"));
                    values.put(DataContract.CharityOrders.COLUMN_POSTED_DATE, jObj.getString("posted_date"));
                    values.put(DataContract.CharityOrders.COLUMN_VENDOR_LOGO_URL, jObj.getString("vendor_logo_url"));
                    values.put(DataContract.CharityOrders.COLUMN_PURCHASE_TOTAL, jObj.getDouble("purchase_total"));
                    values.put(DataContract.CharityOrders.COLUMN_CAUSE_NAME, jObj.getString("cause_name"));
                    values.put(DataContract.CharityOrders.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                    listValues.add(values);
                }
                DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                handler.startBulkInsert(DataInsertHandler.CHARITY_ORDERS_TOKEN, false, DataContract.URI_CHARITY_ORDERS, listValues.toArray(new ContentValues[listValues.size()]));
            } catch (JSONException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new OrdersEvent(false, "No orders data"));
            }
        }
    }
}
