package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.R;
import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.Coupon;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Merchant;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.CouponsDeserializer;
import com.cashback.rest.adapter.MerchantsDeserializer;
import com.cashback.rest.event.CouponsEvent;
import com.cashback.rest.event.MerchantsEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CouponsRequest extends ServiceGenerator<IMerchants> {
    //    private Call<List<Coupon>> call;
//    private Type listType;
//    private Gson gson1;
    private Context context;

//    {
//        listType = new TypeToken<List<Coupon>>() {
//        }.getType();
//        gson1 = new GsonBuilder()
//                .excludeFieldsWithoutExposeAnnotation()
//                .registerTypeAdapter(listType, new CouponsDeserializer()).create();
//    }

    public CouponsRequest(Context ctx) {
        super(IMerchants.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        new CouponsRequestTask().execute();
//        call = createService(gson1).getAllFeatured();
//        call.enqueue(new Callback<List<Coupon>>() {
//            @Override
//            public void onResponse(Call<List<Coupon>> call, Response<List<Coupon>> response) {
//                if (response.isSuccessful()) {
//                    List<Coupon> listCoupon = response.body();
//                    List<ContentValues> listCouponsValues = new ArrayList<>(listCoupon.size());
//                    ContentValues values;
//
//                    for (Coupon coupon : listCoupon) {
//                        values = new ContentValues();
//                        values.put(DataContract.Coupons.COLUMN_COUPON_ID, coupon.getCouponId());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_ID, coupon.getVendorId());
//                        values.put(DataContract.Coupons.COLUMN_COUPON_TYPE, String.valueOf(coupon.getCouponType()));
//                        values.put(DataContract.Coupons.COLUMN_RESTRICTIONS, coupon.getRestrictions());
//                        values.put(DataContract.Coupons.COLUMN_COUPON_CODE, coupon.getCouponCode());
//                        values.put(DataContract.Coupons.COLUMN_EXPIRATION_DATE, coupon.getExpirationDate());
//                        values.put(DataContract.Coupons.COLUMN_AFFILIATE_URL, coupon.getAffiliateUrl());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_LOGO_URL, coupon.getVendorLogoUrl());
//                        values.put(DataContract.Coupons.COLUMN_VENDOR_COMMISSION, coupon.getVendorCommission());
//                        listCouponsValues.add(values);
//                    }
//                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
//                    handler.startBulkInsert(DataInsertHandler.COUPONS_TOKEN, false, DataContract.URI_COUPONS, listCouponsValues.toArray(new ContentValues[listCouponsValues.size()]));
//
//                } else {
//                    int statusCode = response.code();
//                    String answer = "Code " + statusCode + " . ";
//                    ResponseBody errorBody = response.errorBody();
//                    try {
//                        answer += errorBody.string();
//                        errorBody.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    EventBus.getDefault().post(new CouponsEvent(false, answer));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Coupon>> call, Throwable t) {
//                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
//                    ErrorResponse err = ((ErrorRestException) t).getBody();
//                    EventBus.getDefault().post(new CouponsEvent(false, err.getMessage()));
//                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
//                    WarningResponse warn = ((WarningRestException) t).getBody();
//                    EventBus.getDefault().post(new CouponsEvent(false, warn.getMessage()));
//                } else {
//                    EventBus.getDefault().post(new CouponsEvent(false, t.getMessage()));
//                }
//            }
//        });
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
