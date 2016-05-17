package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

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
import com.cashback.rest.event.MerchantsEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by ivansv on 16.04.2016.
 */
public class MerchantsRequest extends ServiceGenerator<IMerchants> {
    private Call<List<Merchant>> call;
    private Type listType;
    private Gson gson1;
    private Context context;

    {
        listType = new TypeToken<List<Merchant>>() {
        }.getType();
        gson1 = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new MerchantsDeserializer()).create();
    }

    public MerchantsRequest(Context ctx) {
        super(IMerchants.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        call = createService(gson1).getMerchants();
        call.enqueue(new Callback<List<Merchant>>() {
            @Override
            public void onResponse(Call<List<Merchant>> call, Response<List<Merchant>> response) {
                if (response.isSuccess()) {
                    List<Merchant> listMerchant = response.body();
                    List<ContentValues> listMerchantsValues = new ArrayList<>(listMerchant.size());
                    ContentValues values;

                    for (Merchant m : listMerchant) {
                        values = new ContentValues();
                        values.put(DataContract.Merchants.COLUMN_VENDOR_ID, m.getVendorId());
                        values.put(DataContract.Merchants.COLUMN_NAME, m.getName());
                        values.put(DataContract.Merchants.COLUMN_COMMISSION, m.getCommission());
                        values.put(DataContract.Merchants.COLUMN_EXCEPTION_INFO, m.getExceptionInfo());
                        values.put(DataContract.Merchants.COLUMN_DESCRIPTION, m.getDescription());
                        values.put(DataContract.Merchants.COLUMN_GIFT_CARD, m.isGiftCard());
                        values.put(DataContract.Merchants.COLUMN_AFFILIATE_URL, m.getAffiliateUrl());
                        values.put(DataContract.Merchants.COLUMN_LOGO_URL, m.getLogoUrl());
                        listMerchantsValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    if (!DataInsertHandler.IS_FILLING_MERCHANT_TABLE) {
                        handler.startBulkInsert(DataInsertHandler.MERCHANTS_TOKEN, false, DataContract.URI_MERCHANTS, listMerchantsValues.toArray(new ContentValues[listMerchantsValues.size()]));
                    }
                } else {
                    int statusCode = response.code();
                    String answer = "Code " + statusCode + " . ";
                    ResponseBody errorBody = response.errorBody();
                    try {
                        answer += errorBody.string();
                        errorBody.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    EventBus.getDefault().post(new MerchantsEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Call<List<Merchant>> call, Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new MerchantsEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new MerchantsEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new MerchantsEvent(false, t.getMessage()));
                }
            }
        });
    }
}
