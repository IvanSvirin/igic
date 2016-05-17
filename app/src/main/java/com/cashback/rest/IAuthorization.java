package com.cashback.rest;

import com.cashback.model.AuthObject;
import com.cashback.model.CharityAccount;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;


/**
 * Created by I.Svirin on 5/12/2016.
 */
public interface IAuthorization {

    @POST("api/v1/authorization/login/")
    Call<CharityAccount> logIn(@Header("IDFA") String idfa, @Body AuthObject authObject);

    @POST("api/v1/authorization/signup/")
    Call<CharityAccount> signUp(@Header("IDFA") String idfa, @Body AuthObject authObject);
}
