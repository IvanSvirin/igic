package com.cashback.rest.request;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.model.Coupon;
import com.cashback.model.Merchant;
import com.cashback.model.Product;
import com.cashback.rest.event.SearchEvent;

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

import de.greenrobot.event.EventBus;

public class SearchRequest {
    private Context context;
    private String searchingWord;
    private ArrayList<Merchant> storesArray;
    private ArrayList<Product> productsArray;
    private ArrayList<Coupon> dealsArray;


    public SearchRequest(Context ctx, String searchingWord, ArrayList<Merchant> storesArray, ArrayList<Product> productsArray, ArrayList<Coupon> dealsArray) {
        this.context = ctx;
        this.searchingWord = searchingWord;
        this.storesArray = storesArray;
        this.productsArray = productsArray;
        this.dealsArray = dealsArray;
    }

    public void fetchData() {
        new SearchRequestTask().execute();
    }

    private class SearchRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        private JSONArray jsonArray;
        private JSONArray storesJsonArray;
        private JSONArray productsJsonArray;
        private JSONArray dealsJsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                searchingWord = Utilities.replaceSpace(searchingWord);
                url = new URL(context.getString(R.string.search_path) + searchingWord);
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
                try {
                    jObj = jsonArray.getJSONObject(0);
                    storesJsonArray = jObj.getJSONArray("stores");
                    productsJsonArray = jObj.getJSONArray("products");
                    dealsJsonArray = jObj.getJSONArray("deals");
                    Merchant merchant;
                    for (int i = 0; i < storesJsonArray.length(); i++) {
                        jObj = storesJsonArray.getJSONObject(i);
                        merchant = new Merchant();
                        merchant.setCommission((float) jObj.getDouble("commission"));
                        merchant.setAffiliateUrl(jObj.getString("affiliate_url"));
                        merchant.setFavorite(jObj.getInt("is_favorite") == 1);
                        merchant.setDescription(jObj.getString("description"));
                        merchant.setLogoUrl(jObj.getString("logo_url"));
                        merchant.setGiftCard(jObj.getInt("gift_card") == 1);
                        merchant.setExceptionInfo(jObj.getString("exception_info"));
                        merchant.setName(jObj.getString("name"));
                        merchant.setVendorId(jObj.getLong("vendor_id"));
                        if (jObj.has("owners_benefit")) {
                            merchant.setOwnersBenefit(jObj.getBoolean("owners_benefit"));
                        } else {
                            merchant.setOwnersBenefit(false);
                        }
                        storesArray.add(merchant);
                    }
                    Product product;
                    for (int i = 0; i < productsJsonArray.length(); i++) {
                        jObj = productsJsonArray.getJSONObject(i);
                        product = new Product();
                        product.setVendorId(jObj.getLong("vendor_id"));
                        product.setTitle(jObj.getString("title"));
                        product.setPrice((float) jObj.getDouble("price"));
//                        product.setDescription(jObj.getString("description"));
                        product.setImageUrl(jObj.getString("image_url"));
                        product.setVendorLogoUrl(jObj.getString("vendor_logo_url"));
                        product.setVendorCommission((float) jObj.getDouble("vendor_commission"));
                        product.setVendorAffiliateUrl(jObj.getString("vendor_affiliate_url"));
                        productsArray.add(product);
                    }
                    Coupon coupon;
                    for (int i = 0; i < dealsJsonArray.length(); i++) {
                        jObj = dealsJsonArray.getJSONObject(i);
                        coupon = new Coupon();
                        coupon.setCouponId(jObj.getLong("coupon_id"));
                        coupon.setVendorId(jObj.getLong("vendor_id"));
                        coupon.setCouponType(jObj.getString("coupon_type"));
                        coupon.setRestrictions(jObj.getString("restrictions"));
                        coupon.setCouponCode(jObj.getString("coupon_code"));
                        coupon.setExpirationDate(jObj.getString("expiration_date"));
                        coupon.setAffiliateUrl(jObj.getString("affiliate_url"));
                        coupon.setVendorLogoUrl(jObj.getString("vendor_logo_url"));
                        coupon.setLabel(jObj.getString("label"));
                        coupon.setVendorCommission((float) jObj.getDouble("vendor_commission"));
                        if (jObj.has("owners_benefit")) {
                            coupon.setOwnersBenefit(jObj.getBoolean("owners_benefit"));
                        } else {
                            coupon.setOwnersBenefit(false);
                        }
                        dealsArray.add(coupon);
                    }
                    EventBus.getDefault().post(new SearchEvent(true, "OK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                EventBus.getDefault().post(new SearchEvent(false, "Nothing was searched!"));
            }
        }
    }
}
