package com.cashback.rest;

import com.cashback.model.Category;
import com.cashback.model.Coupon;
import com.cashback.model.Extra;
import com.cashback.model.Merchant;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


public interface IMerchants {

    @GET("api/v1/merchants/")
    Call<List<Merchant>> getMerchants();

    @GET("api/v1/merchants/extra/")
    Call<List<Extra>> getExtras();

    @GET("api/v1/merchants/coupons/featured/")
    Call<List<Coupon>> getAllFeatured();

    @GET("api/v1/categories/")
    Call<List<Category>> getCategories();
}
