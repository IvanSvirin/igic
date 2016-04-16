package com.cashback.rest;

import com.cashback.model.Merchant;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;

/**
 * Created by ivansv on 16.04.2016.
 */
public interface IMerchants {

    @GET("/api/v1/merchants/")
    Call<List<Merchant>> getAllMerchants();
}
