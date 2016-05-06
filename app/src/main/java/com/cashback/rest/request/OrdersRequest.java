package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Order;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IAccount;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.OrdersDeserializer;
import com.cashback.rest.event.OrdersEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by I.Svirin on 5/5/2016.
 */
public class OrdersRequest extends ServiceGenerator<IAccount> {
    private Call<List<Order>> call;
    private Type listType;
    private Gson gson1;
    private Context context;

    {
        listType = new TypeToken<List<Order>>() {
        }.getType();
        gson1 = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new OrdersDeserializer()).create();
    }

    public OrdersRequest(Context ctx) {
        super(IAccount.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        call = createService(gson1).getOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Response<List<Order>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Order> listOrder = response.body();
                    List<ContentValues> listOrdersValues = new ArrayList<>(listOrder.size());
                    ContentValues values;

                    for (Order order : listOrder) {
                        values = new ContentValues();
                        values.put(DataContract.Orders.COLUMN_VENDOR_ID, order.getVendorId());
                        values.put(DataContract.Orders.COLUMN_PURCHASE_TOTAL, order.getPurchaseTotal());
                        values.put(DataContract.Orders.COLUMN_CONFIRMATION_NUMBER, order.getConfirmationNumber());
                        values.put(DataContract.Orders.COLUMN_ORDER_DATE, order.getOrderDate());
                        values.put(DataContract.Orders.COLUMN_POSTED_DATE, order.getPostedDate());
                        values.put(DataContract.Orders.COLUMN_VENDOR_NAME, order.getVendorName());
                        values.put(DataContract.Orders.COLUMN_VENDOR_LOGO_URL, order.getVendorLogoUrl());
                        values.put(DataContract.Orders.COLUMN_SHARED_STOCK_AMOUNT, order.getSharedStockAmount());
                        values.put(DataContract.Orders.COLUMN_CASH_BACK, order.getCashBack());
                        listOrdersValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.ORDERS_TOKEN, false, DataContract.URI_ORDERS,
                            listOrdersValues.toArray(new ContentValues[listOrdersValues.size()]));
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
                    EventBus.getDefault().post(new OrdersEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new OrdersEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new OrdersEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new OrdersEvent(false, t.getMessage()));
                }
            }
        });
    }
}
