package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.event.CouponsEvent;

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

public class CouponsRequest extends ServiceGenerator<IMerchants> {
    private Context context;

    public CouponsRequest(Context ctx) {
        super(IMerchants.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        new CouponsRequestTask().execute();
    }

    private class CouponsRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.coupons_path));
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("IDFA", Utilities.retrieveIdfa(context));
                urlConnection.setRequestProperty("token", Utilities.retrieveUserToken(context));

                inputStream = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new CouponsEvent(false, "No coupons featured data"));
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
                EventBus.getDefault().post(new CouponsEvent(false, "No coupons featured data"));
            }
            try {
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                EventBus.getDefault().post(new CouponsEvent(false, "No coupons featured data"));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jsonArray != null) {
                List<ContentValues> listCouponsValues = new ArrayList<>(jsonArray.length());
                ContentValues values;
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        values = new ContentValues();
                        values.put(DataContract.Coupons.COLUMN_COUPON_ID, jObj.getLong("coupon_id"));
                        values.put(DataContract.Coupons.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                        values.put(DataContract.Coupons.COLUMN_COUPON_TYPE, jObj.getString("coupon_type"));
                        values.put(DataContract.Coupons.COLUMN_RESTRICTIONS, jObj.getString("restrictions"));
                        values.put(DataContract.Coupons.COLUMN_LABEL, jObj.getString("label"));
                        values.put(DataContract.Coupons.COLUMN_COUPON_CODE, jObj.getString("coupon_code"));
                        values.put(DataContract.Coupons.COLUMN_EXPIRATION_DATE, jObj.getString("expiration_date"));
                        values.put(DataContract.Coupons.COLUMN_AFFILIATE_URL, jObj.getString("affiliate_url"));
                        values.put(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL, jObj.getString("vendor_logo_url"));
                        values.put(DataContract.Coupons.COLUMN_VENDOR_COMMISSION, jObj.getDouble("vendor_commission"));
                        listCouponsValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, false, DataContract.URI_COUPONS, listCouponsValues.toArray(new ContentValues[listCouponsValues.size()]));

                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new CouponsEvent(false, "No coupons featured data"));
                }
            }
        }
    }
}
