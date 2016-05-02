package com.cashback.rest.request;

import com.cashback.model.Payment;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by ivansv on 02.05.2016.
 */
public interface IAccount {

    @GET("/api/v1/account/history/payment/")
    Call<List<Payment>> getPayments();
}
