package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Coupon;
import com.cashback.rest.event.FavoritesEvent;
import com.cashback.rest.event.MerchantCouponsEvent;

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
 * Created by I.Svirin on 5/24/2016.
 */
public class CouponsByMerchantIdRequest {
    private Context context;
    private long id;
    private ArrayList<Coupon> coupons;

    public CouponsByMerchantIdRequest(Context ctx, long id, ArrayList<Coupon> coupons) {
        this.context = ctx;
        this.id = id;
        this.coupons = coupons;
    }

    public void fetchData() {
        new CouponsByMerchantIdRequestTask().execute();
    }

    private class CouponsByMerchantIdRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/merchants/" + String.valueOf(id) + "/coupons/");
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
            Coupon coupon;
            if (jsonArray != null) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        coupon = new Coupon();
                        coupon.setCouponId(jObj.getLong("coupon_id"));
                        coupon.setVendorId(jObj.getLong("vendor_id"));
                        coupon.setCouponType(jObj.getString("coupon_type"));
                        coupon.setRestrictions(jObj.getString("restrictions"));
                        coupon.setCouponCode(jObj.getString("coupon_code"));
                        coupon.setExpirationDate(jObj.getString("expiration_date"));
                        coupon.setAffiliateUrl(jObj.getString("affiliate_url"));
                        coupon.setVendorLogoUrl(jObj.getString("vendor_logo_url"));
                        coupon.setVendorCommission((float) jObj.getDouble("vendor_commission"));
                        coupons.add(coupon);
                    }
                    EventBus.getDefault().post(new MerchantCouponsEvent(true, "OK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new MerchantCouponsEvent(false, "No coupons featured data"));
                }
            }
        }
    }
}
