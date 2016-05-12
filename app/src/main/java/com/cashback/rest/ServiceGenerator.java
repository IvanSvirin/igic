package com.cashback.rest;

import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by ivansv on 16.04.2016.
 */
public abstract class ServiceGenerator<S> {

    private static final long CONNECT_TIMEOUT = 10;
    private static final long READ_TIMEOUT = 3;

    @Retention(RetentionPolicy.CLASS)
    @StringDef({REQUEST_STATUS_OK, REQUEST_STATUS_ERROR, REQUEST_STATUS_SERVER_ERROR,
            REQUEST_STATUS_WARNING, REQUEST_STATUS_UNKNOWN})
    public @interface RequestStatusCode {
    }

    public final static String REQUEST_STATUS_OK = "success";
    public final static String REQUEST_STATUS_ERROR = "error";
    public final static String REQUEST_STATUS_WARNING = "warning";
    public final static String REQUEST_STATUS_NETWORK_ERROR = "net_error";
    public final static String REQUEST_STATUS_SERVER_ERROR = "server_error";
    public final static String REQUEST_STATUS_UNKNOWN = "unknown";

    // TODO: 4/19/2016 TEST - will be deleted
//    public static final String API_PRODUCTION_URL = "http://www.greatcanadianrebates.ca";
    public static final String API_PRODUCTION_URL = "http://beta1.igive.com/rest/iGive";
//    public static final String API_PRODUCTION_URL = "http://www.tm.iconsumer.com";

    protected Class<S> serviceClass;
    protected Gson gson;

    public ServiceGenerator(Class<S> serviceClazz) {
        this.serviceClass = serviceClazz;
    }

    private OkHttpClient httpClient = new OkHttpClient();

    public S createService(@NonNull Gson gson) {
        httpClient.setConnectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);
        httpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.MINUTES);
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(API_PRODUCTION_URL)
                .addConverterFactory(GsonConverterFactory.create(gson));

        Retrofit retrofit = builder.client(httpClient).build();
        return retrofit.create(serviceClass);
    }

    public abstract void fetchData();
}

