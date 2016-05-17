package com.cashback.rest;

import com.cashback.model.Order;
import com.cashback.model.Payment;
import com.cashback.model.Trip;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by ivansv on 02.05.2016.
 */
public interface IAccount {

    @GET("api/v1/account/history/payment/")
    Call<List<Payment>> getPayments();

    @GET("api/v1/account/history/trip/")
    Call<List<Trip>> getShoppingTrips();

    @GET("api/v1/account/history/order/")
    Call<List<Order>> getOrders();
}
