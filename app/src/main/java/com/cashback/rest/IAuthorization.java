package com.cashback.rest;

import com.cashback.Utilities;
import com.cashback.model.AuthObject;
import com.cashback.model.CharityAccount;

import retrofit.Call;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Header;
import retrofit.http.Headers;
import retrofit.http.POST;

/**
 * Created by I.Svirin on 5/12/2016.
 */
public interface IAuthorization {
    @POST("/api/v1/authorization/login/")
    Call<CharityAccount> logIn(@Header("IDFA") String idfa, @Body AuthObject authObject);
//    https://tm.igive.com/cfc/iGiveAppAPIs.cfc?method=login&email=ivan_svirin@mail.ru&password=i12472
}
