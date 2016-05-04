package com.cashback.rest.request;

import android.content.ContentValues;
import android.content.Context;

import com.cashback.db.DataContract;
import com.cashback.db.DataInsertHandler;
import com.cashback.model.ErrorResponse;
import com.cashback.model.Payment;
import com.cashback.model.Trip;
import com.cashback.model.WarningResponse;
import com.cashback.rest.ErrorRestException;
import com.cashback.rest.IAccount;
import com.cashback.rest.ServiceGenerator;
import com.cashback.rest.WarningRestException;
import com.cashback.rest.adapter.PaymentsDeserializer;
import com.cashback.rest.adapter.ShoppingTripsDeserializer;
import com.cashback.rest.event.PaymentsEvent;
import com.cashback.rest.event.ShoppingTripsEvent;
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
 * Created by I.Svirin on 5/4/2016.
 */
public class ShoppingTripsRequest extends ServiceGenerator<IAccount> {
    private Call<List<Trip>> call;
    private Type listType;
    private Gson gson1;
    private Context context;

    {
        listType = new TypeToken<List<Trip>>() {
        }.getType();
        gson1 = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(listType, new ShoppingTripsDeserializer()).create();
    }

    public ShoppingTripsRequest(Context ctx) {
        super(IAccount.class);
        this.context = ctx;
    }

    @Override
    public void fetchData() {
        call = createService(gson1).getShoppingTrips();
        call.enqueue(new Callback<List<Trip>>() {
            @Override
            public void onResponse(Response<List<Trip>> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    List<Trip> listShoppingTrip = response.body();
                    List<ContentValues> listShoppingTripsValues = new ArrayList<>(listShoppingTrip.size());
                    ContentValues values;

                    for (Trip trip : listShoppingTrip) {
                        values = new ContentValues();
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_ID, trip.getVendorId());
                        values.put(DataContract.ShoppingTrips.COLUMN_CONFIRMATION_NUMBER, trip.getConfirmationNumber());
                        values.put(DataContract.ShoppingTrips.COLUMN_TRIP_DATE, trip.getTripDate());
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_NAME, trip.getVendorName());
                        values.put(DataContract.ShoppingTrips.COLUMN_VENDOR_LOGO_URL, trip.getVendorLogoUrl());
                        listShoppingTripsValues.add(values);
                    }
                    DataInsertHandler handler = new DataInsertHandler(context, context.getContentResolver());
                    handler.startBulkInsert(DataInsertHandler.SHOPPING_TRIPS_TOKEN, false, DataContract.URI_SHOPPING_TRIPS,
                            listShoppingTripsValues.toArray(new ContentValues[listShoppingTripsValues.size()]));
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
                    EventBus.getDefault().post(new ShoppingTripsEvent(false, answer));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_ERROR)) {
                    ErrorResponse err = ((ErrorRestException) t).getBody();
                    EventBus.getDefault().post(new ShoppingTripsEvent(false, err.getMessage()));
                } else if (t.getMessage() != null && t.getMessage().equals(ServiceGenerator.REQUEST_STATUS_WARNING)) {
                    WarningResponse warn = ((WarningRestException) t).getBody();
                    EventBus.getDefault().post(new ShoppingTripsEvent(false, warn.getMessage()));
                } else {
                    EventBus.getDefault().post(new ShoppingTripsEvent(false, t.getMessage()));
                }
            }
        });
    }
}
