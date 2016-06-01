package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Merchant;
import com.cashback.rest.IMerchants;
import com.cashback.rest.event.CategoryMerchantsEvent;
import com.cashback.rest.event.MerchantCouponsEvent;
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

/**
 * Created by I.Svirin on 6/1/2016.
 */
public class MerchantsByCategoryRequest {
    private Context context;
    private ArrayList<Merchant> merchants;
    private long id;

    public MerchantsByCategoryRequest(Context ctx, ArrayList<Merchant> merchants, long id) {
        this.context = ctx;
        this.merchants = merchants;
        this.id = id;
    }

    public void fetchData() {
        new MerchantsByCategoryRequestTask().execute();
    }

    private class MerchantsByCategoryRequestTask extends AsyncTask<Void, Void, Void> {
        private String jsonString = "";
        JSONArray jsonArray;
        private JSONObject jObj = null;
        private URL url;
        private InputStream inputStream = null;
        private HttpURLConnection urlConnection = null;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/categories/" + String.valueOf(id) + "/merchants/");
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
            Merchant merchant;
            if (jsonArray != null) {
                try {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        jObj = jsonArray.getJSONObject(i);
                        merchant = new Merchant();
                        merchant.setCommission((float) jObj.getDouble("commission"));
                        merchant.setVendorId(jObj.getLong("vendor_id"));
                        merchant.setName(jObj.getString("name"));
                        merchants.add(merchant);
                    }
                    EventBus.getDefault().post(new CategoryMerchantsEvent(true, "OK"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    EventBus.getDefault().post(new CategoryMerchantsEvent(false, "No merchants featured data"));
                }
            } else {
                EventBus.getDefault().post(new CategoryMerchantsEvent(false, "No merchants featured data"));
            }
        }
    }
}
