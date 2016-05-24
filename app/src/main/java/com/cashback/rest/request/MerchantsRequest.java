package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.cashback.Utilities;
import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Merchant;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IMerchants;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
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

/**
 * Created by ivansv on 16.04.2016.
 */
public class MerchantsRequest extends ServiceGenerator<IMerchants> {
//    private Call<List<Merchant>> call;
//    private Type listType;
//    private Gson gson1;
    private Context context;

//    {
//        listType = new TypeToken<List<Merchant>>() {
//        }.getType();
//        gson1 = new GsonBuilder()
//                .setLenient()
//                .excludeFieldsWithoutExposeAnnotation()
//                .registerTypeAdapter(listType, new MerchantsDeserializer())
//                .create();
//    }
//
    public MerchantsRequest(Context ctx) {
        super(IMerchants.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        new MerchantsRequestTask().execute();

//        call = createService(gson1).getMerchants();
//        call.enqueue(new Callback<List<Merchant>>() {
//            @Override
//            public void onResponse(Call<List<Merchant>> call, Response<List<Merchant>> response) {
//                if (response.isSuccessful()) {
//                    List<Merchant> listMerchant = response.body();
//                    List<ContentValues> listMerchantsValues = new ArrayList<>(listMerchant.size());
//                    ContentValues values;
//
//                    for (Merchant m : listMerchant) {
//                        values = new ContentValues();
//                        values.put(DataContract.Merchants.COLUMN_VENDOR_ID, m.getVendorId());
//                        values.put(DataContract.Merchants.COLUMN_NAME, m.getName());
//                        values.put(DataContract.Merchants.COLUMN_COMMISSION, m.getCommission());
//                        values.put(DataContract.Merchants.COLUMN_EXCEPTION_INFO, m.getExceptionInfo());
//                        values.put(DataContract.Merchants.COLUMN_DESCRIPTION, m.getDescription());
//                        values.put(DataContract.Merchants.COLUMN_GIFT_CARD, m.isGiftCard());
//                        values.put(DataContract.Merchants.COLUMN_AFFILIATE_URL, m.getAffiliateUrl());
//                        values.put(DataContract.Merchants.COLUMN_LOGO_URL, m.getLogoUrl());
//                        listMerchantsValues.add(values);
//                    }
//                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
//                    if (!DataInsertHandler.IS_FILLING_MERCHANT_TABLE) {
//                        handler.startBulkInsert(DataInsertHandler.MERCHANTS_TOKEN, false, DataContract.URI_MERCHANTS, listMerchantsValues.toArray(new ContentValues[listMerchantsValues.size()]));
//                    }
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
//                    EventBus.getDefault().post(new MerchantsEvent(false, answer));
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Merchant>> call, Throwable t) {
//                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
//                    ErrorResponse err = ((ErrorRestException) t).getBody();
//                    EventBus.getDefault().post(new MerchantsEvent(false, err.getMessage()));
//                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
//                    WarningResponse warn = ((WarningRestException) t).getBody();
//                    EventBus.getDefault().post(new MerchantsEvent(false, warn.getMessage()));
//                } else {
//                    EventBus.getDefault().post(new MerchantsEvent(false, t.getMessage()));
//                }
//            }
//        });
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
                url = new URL("https://beta1.igive.com/rest/iGive/api/v1/merchants/");
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
                    values.put(DataContract.Merchants.COLUMN_COMMISSION, jObj.getDouble("commission"));
                    values.put(DataContract.Merchants.COLUMN_AFFILIATE_URL, jObj.getString("affiliate_url"));
                    values.put(DataContract.Merchants.COLUMN_IS_FAVORITE, jObj.getInt("is_favorite"));
                    values.put(DataContract.Merchants.COLUMN_DESCRIPTION, jObj.getString("description"));
                    values.put(DataContract.Merchants.COLUMN_LOGO_URL, jObj.getString("logo_url"));
                    values.put(DataContract.Merchants.COLUMN_GIFT_CARD, jObj.getInt("gift_card"));
                    values.put(DataContract.Merchants.COLUMN_EXCEPTION_INFO, jObj.getString("exception_info"));
                    values.put(DataContract.Merchants.COLUMN_VENDOR_ID, jObj.getLong("vendor_id"));
                    values.put(DataContract.Merchants.COLUMN_NAME, jObj.getString("name"));
                    listValues.add(values);
                }
                DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                handler.startBulkInsert(DataInsertHandler.MERCHANTS_TOKEN, false, DataContract.URI_MERCHANTS, listValues.toArray(new ContentValues[listValues.size()]));
            } catch (JSONException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MerchantsEvent(false, "No merchants featured data"));
            }
        }

    }
}
