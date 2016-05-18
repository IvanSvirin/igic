package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Payment;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IAccount;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.PaymentsDeserializer;
import com.cashback.rest.event.PaymentsEvent;
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
 * Created by ivansv on 02.05.2016.
 */
public class PaymentsRequest extends ServiceGenerator<IAccount> {
    private Call<List<Payment>> call;
    private Type listType;
    private Gson gson1;
    private Context context;

    {
        listType = new TypeToken<List<Payment>>() {
        }.getType();
        gson1 = new GsonBuilder()
                .setLenient()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new PaymentsDeserializer()).create();
    }

    public PaymentsRequest(Context ctx) {
        super(IAccount.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        call = createService(gson1).getPayments();
        call.enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if (response.isSuccessful()) {
                    List<Payment> listPayment = response.body();
                    List<ContentValues> listPaymentsValues = new ArrayList<>(listPayment.size());
                    ContentValues values;

                    for (Payment payment : listPayment) {
                        values = new ContentValues();
                        values.put(DataContract.Payments.COLUMN_PAYMENT_DATE, payment.getPaymentDate());
                        values.put(DataContract.Payments.COLUMN_PAYMENT_AMOUNT, payment.getPaymentAmount());
                        values.put(DataContract.Payments.COLUMN_PAYMENT_ACCOUNT, payment.getPaymentAccount());
                        listPaymentsValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.PAYMENTS_TOKEN, false, DataContract.URI_PAYMENTS,
                            listPaymentsValues.toArray(new ContentValues[listPaymentsValues.size()]));
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
                    EventBus.getDefault().post(new PaymentsEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new PaymentsEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new PaymentsEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new PaymentsEvent(false, t.getMessage()));
                }
            }
        });
    }
}
