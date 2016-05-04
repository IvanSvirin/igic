package com.cashback.rest;

import com.cashback.model.Payment;
import com.cashback.model.Trip;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by ivansv on 02.05.2016.
 */
public interface IAccount {

    @GET("/api/v1/account/history/payment/")
    Call<List<Payment>> getPayments();

    @GET("/api/v1/account/history/trip/")
    Call<List<Trip>> getShoppingTrips();
}
