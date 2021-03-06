package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataInsertHandler;

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
import java.util.Set;

import db.DataContract;

public class AllUsedCouponsRequest {
    private Context context;
    private int counter = 0;
    private int size = 0;
    private List<ContentValues> listCouponsValues;

    public AllUsedCouponsRequest(Context ctx) {
        this.context = ctx;
    }

    public void fetchData() {
        listCouponsValues = new ArrayList<>();
        Set<String> set = Utilities.retrieveVendorIdSet(context);
        if (set != null) {
            size = set.size();
            for (String s : set) {
                new CouponsByMerchantIdRequestTask(s).execute();
            }
        }
    }

    private class CouponsByMerchantIdRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;
        private String id;

        public CouponsByMerchantIdRequestTask(String id) {
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL(context.getString(R.string.merchants_path) + id + "/coupons/");
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
                jsonArray = new JSONArray(jsonString);
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (jsonArray != null) {
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
                        if (jObj.has("owners_benefit")) {
                            if (jObj.getBoolean("owners_benefit")) {
                                values.put(DataContract.Coupons.COLUMN_OWNERS_BENEFIT, 1);
                            }
                        } else {
                            values.put(DataContract.Coupons.COLUMN_OWNERS_BENEFIT, 0);
                        }
                        listCouponsValues.add(values);
                    }
                    counter++;
                    if (counter == size) {
                        DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                        handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, false, DataContract.URI_COUPONS, listCouponsValues.toArray(new ContentValues[listCouponsValues.size()]));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                counter++;
                if (counter == size) {
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, false, DataContract.URI_COUPONS, listCouponsValues.toArray(new ContentValues[listCouponsValues.size()]));
                }
            }
        }
    }
}
