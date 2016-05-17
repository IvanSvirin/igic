package com.cashback.rest;

import com.cashback.model.Category;
import com.cashback.model.Coupon;
import com.cashback.model.Merchant;
import com.cashback.model.OfferCoupon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;


/**
 * Created by ivansv on 16.04.2016.
 */
public interface IMerchants {

    @GET("api/v1/merchants/")
    Call<List<Merchant>> getMerchants();

    @GET("api/v1/merchants/coupons/featured/")
    Call<List<Coupon>> getAllFeatured();

    @GET("api/v1/categories/")
    Call<List<Category>> getCategories();


    // TODO: 4/19/2016 TEST - will be deleted
    @GET("api/2/offers/")
    Call<List<OfferCoupon>> getOfferCoupons();
}
